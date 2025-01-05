package com.example.lineup2025

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.lineup2025.model.Location
import com.example.lineup2025.utils.NetworkResult
import com.example.lineup2025.viewmodel.AccessAvatarViewModel
import java.util.Locale
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class RadarView : View {

    private lateinit var context: Context
    private var viewModel: AccessAvatarViewModel? = null

    val drawableMap = mapOf(
        1 to R.drawable.red_avatar,
        2 to R.drawable.pink_avatar,
        3 to R.drawable.yellow_avatar,
        4 to R.drawable.small_avatar,
        5 to R.drawable.grey_avatar,
        6 to R.drawable.blue_avatar,
        7 to R.drawable.brown_avatar,
        8 to R.drawable.green_avatar
    )

    private var users: List<Location> = emptyList()
    private var selfDrawable: Bitmap? = null

    private var radarSize: Int = 0

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.context = context
    }

    fun setUsers(users: List<Location>) {
        this.users = users
        invalidate() // Trigger redraw
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Calculate the size of the radar view, assuming it is square
        radarSize = min(w, h)
        // Initialize the drawable when the size is available
        init()

    }

    private fun init() {
        if (radarSize == 0) return
        viewModel?.accessAvatar() ?: Log.e("RadarView", "ViewModel is null")
    }

    fun setupViewModel(viewModel: AccessAvatarViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        setupObservers(lifecycleOwner)
        init()
    }

    private fun setupObservers(viewLifecycleOwner: LifecycleOwner) {
        viewModel?.accessAvatarResponseLiveData?.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    val responseBody = it.data
                    Log.d("RadarView", "Response Body: $responseBody")
                    responseBody?.let { data ->
                        val avatarId = data.avatar

                        // Now that you have the avatarId, perform further actions
                        val originalDrawable = BitmapFactory.decodeResource(
                            resources,
                            drawableMap[avatarId] ?: return@let
                        )
                        val radarSize = min(width, height)
                        val maxDrawableDimension = (radarSize * 0.09).toInt()
                        val scaleFactor = maxDrawableDimension.toFloat() / max(
                            originalDrawable.width,
                            originalDrawable.height
                        )
//                    selfDrawable = Bitmap.createScaledBitmap(originalDrawable, (originalDrawable.width * scaleFactor).toInt(), (originalDrawable.height * scaleFactor).toInt(), true)
                        val newWidth = (originalDrawable.width * scaleFactor).toInt()
                        val newHeight = (originalDrawable.height * scaleFactor).toInt()

                        if (newWidth > 0 && newHeight > 0) {
                            selfDrawable = Bitmap.createScaledBitmap(
                                originalDrawable,
                                newWidth,
                                newHeight,
                                true
                            )
                        } else {
                            Log.e(
                                "RadarView",
                                "Invalid dimensions for bitmap: width=$newWidth, height=$newHeight"
                            )
                        }
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                    Log.d("RadarView", "Error: ${it.message}")
                }

                is NetworkResult.Loading -> {
                    Log.d("RadarView", "Loading...")
                }
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw radar circle
        val centerX = width / 2
        val centerY = height / 2f
        // Calculate maxDistanceInKm dynamically based on the actual distance values of users
        val maxDistanceInKm = users.maxByOrNull { it.distance }?.distance
            ?: 1.0 // Default to 100 km if no users are present
        //  val maxDistanceInKm = 1 // Maximum distance in kilometers (adjust as needed)
        val maxRadarDimension = min(width, height) // Assuming radar view is square
        val maxRadarRadius = maxRadarDimension / 2
        val scalingFactor = maxRadarRadius / maxDistanceInKm


        // canvas.drawCircle(centerX, centerY, maxRadarRadius.toFloat(), radarPaint)
        val drawableWidth = selfDrawable?.width ?: 0
        val drawableHeight = selfDrawable?.height ?: 0
        val x = centerX - drawableWidth / 2f
        val y = centerY - drawableHeight / 2f

        selfDrawable?.let {
            canvas.drawBitmap(it, x, y, null)
        }
        val sortedUsers = users.sortedBy { it.distance }

        // Draw users

        for (user in sortedUsers) {
            val directionInDegrees =
                directionToDegrees(user.direction) // Direction of the user in degrees

            val separationFactor = 0.35 // Adjust this factor as needed
            var distance = maxDistanceInKm * (0.5 + (sortedUsers.indexOf(user) * separationFactor))
            // Calculate position of the user based on distance and direction
            var x =
                centerX + distance * scalingFactor * cos(Math.toRadians(directionInDegrees)).toFloat()
            var y =
                centerY + distance * scalingFactor * sin(Math.toRadians(directionInDegrees)).toFloat()


            //canvas.drawCircle(x.toFloat(), y.toFloat(), 20f, userPaint)
            val userLayout = createUserLayout(user, maxRadarDimension)
            userLayout.measure(width, height)
            userLayout.layout(0, 0, width, height)
            val userLayoutWidth = userLayout.measuredWidth
            val userLayoutHeight = userLayout.measuredHeight

            // Adjust user position if it exceeds the view bounds
            if (x < 0) {
                x = 0.0
            } else if (x + userLayoutWidth > width) {
                x = (width - userLayoutWidth.toFloat()).toDouble()
            }

            if (y < 0) {
                y = 0.0
            } else if (y + userLayoutHeight > height) {
                y = (height - userLayoutHeight.toFloat()).toDouble()
            }
            canvas.save()
            canvas.translate(
                (x - userLayout.measuredWidth / 2f).toFloat(),
                (y - userLayout.measuredHeight / 2f).toFloat()
            )
            userLayout.draw(canvas)
            canvas.restore()

        }
    }

    private fun createUserLayout(user: Location, maxRadarDimension: Int): RelativeLayout {
        val context = context
        val userLayout = RelativeLayout(context)

        val radarSize = min(width, height) // Assuming radar view is square
        val maxDrawableDimension = (radarSize * 0.010).toInt()

        // Name TextView
        val nameTextView = TextView(context)
        nameTextView.id = View.generateViewId() // Set unique id for this view
        nameTextView.text = "${user.name}"
        nameTextView.textSize = maxDrawableDimension.toFloat()// Change to user's actual name
        nameTextView.gravity = Gravity.CENTER_HORIZONTAL
        val nameLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        nameLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        nameLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        nameTextView.layoutParams = nameLayoutParams
        userLayout.addView(nameTextView)

        // Get dimensions of selfDrawable
        val selfDrawableWidth = selfDrawable?.width ?: 0
        val selfDrawableHeight = selfDrawable?.height ?: 0

        if (selfDrawableWidth > 0 && selfDrawableHeight > 0) {
            // Avatar ImageView
            val avatarImageView = ImageView(context)
            avatarImageView.id = View.generateViewId() // Set unique id for this view
            val avatarDrawable = BitmapFactory.decodeResource(
                resources,
                drawableMap[user.avatar] ?: R.drawable.red_avatar
            )
            val scaledAvatar =
                Bitmap.createScaledBitmap(
                    avatarDrawable,
                    selfDrawableWidth,
                    selfDrawableHeight,
                    true
                )
            avatarImageView.setImageBitmap(scaledAvatar)
            val avatarLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            avatarLayoutParams.addRule(
                RelativeLayout.BELOW,
                nameTextView.id
            ) // Position below the name TextView
            avatarLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            avatarImageView.layoutParams = avatarLayoutParams
            userLayout.addView(avatarImageView)

            // Distance TextView
            val distanceTextView = TextView(context)
            distanceTextView.id = View.generateViewId() // Set unique id for this view
            // Calculate distance in meters
            val distanceInMeters = user.distance * 1000 // Convert kilometers to meters

// Determine the appropriate format based on distance
            val formattedDistance = if (distanceInMeters < 1000) {
                "${
                    String.format(
                        "%.0f",
                        distanceInMeters
                    )
                }m" // Format meters without decimal places
            } else {
                "${
                    String.format(
                        "%.2f",
                        user.distance
                    )
                }km" // Format kilometers with 3 decimal places
            }

// Set the text with the formatted distance
            distanceTextView.text = formattedDistance

            distanceTextView.textSize = maxDrawableDimension.toFloat()
            distanceTextView.gravity = Gravity.CENTER_HORIZONTAL
            val distanceLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            distanceLayoutParams.addRule(
                RelativeLayout.BELOW,
                avatarImageView.id
            ) // Position below the avatar ImageView
            distanceLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            distanceTextView.layoutParams = distanceLayoutParams
            userLayout.addView(distanceTextView)

            // Set margins to ensure space between elements
            val margin = 8 // Adjust margin as needed
            val userLayoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            userLayoutParams.setMargins(margin, margin, margin, margin)
            userLayout.layoutParams = userLayoutParams
        }
        return userLayout
    }

    fun directionToDegrees(direction: String): Double {
        return when (direction.uppercase(Locale.ROOT)) {
            "N" -> 270.0
            "NE" -> 315.0
            "E" -> 0.0
            "SE" -> 45.0
            "S" -> 90.0
            "SW" -> 135.0
            "W" -> 180.0
            "NW" -> 225.0
            else -> 0.0 // Default to North if direction is not recognized
        }
    }
}
