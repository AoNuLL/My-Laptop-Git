package com.ai.voicechanger.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.ai.voicechanger.R
import com.ai.voicechanger.ui.activity.MainActivity

class FloatWindowService : Service() {
    
    private lateinit var windowManager: WindowManager
    private lateinit var floatView: View
    private var isShowing = false
    
    companion object {
        const val CHANNEL_ID = "float_window_channel"
        const val NOTIFICATION_ID = 1001
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "悬浮窗服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "用于显示录音悬浮窗"
                setShowBadge(false)
            }
            
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI 变声器")
            .setContentText("悬浮窗服务运行中")
            .setSmallIcon(R.drawable.circle_background)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "SHOW" -> showFloatWindow()
            "HIDE" -> hideFloatWindow()
            "TOGGLE" -> toggleFloatWindow()
        }
        return START_STICKY
    }
    
    fun showFloatWindow() {
        if (isShowing) return
        
        floatView = LayoutInflater.from(this).inflate(R.layout.float_window, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 100
            y = 300
        }
        
        setupFloatViewListeners(floatView, params)
        
        try {
            windowManager.addView(floatView, params)
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun hideFloatWindow() {
        if (!isShowing) return
        
        try {
            windowManager.removeView(floatView)
            isShowing = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun toggleFloatWindow() {
        if (isShowing) {
            hideFloatWindow()
        } else {
            showFloatWindow()
        }
    }
    
    private fun setupFloatViewListeners(view: View, params: WindowManager.LayoutParams) {
        view.findViewById<TextView>(R.id.tv_status)?.text = "准备就绪"
        
        view.findViewById<ImageButton>(R.id.btn_close)?.setOnClickListener {
            hideFloatWindow()
        }
        
        var downX = 0f
        var downY = 0f
        
        view.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    params.x += (event.rawX - downX).toInt()
                    params.y += (event.rawY - downY).toInt()
                    downX = event.rawX
                    downY = event.rawY
                    windowManager.updateViewLayout(floatView, params)
                }
            }
            false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        hideFloatWindow()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}
