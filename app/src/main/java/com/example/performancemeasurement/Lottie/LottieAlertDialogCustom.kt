package com.labters.lottiealertdialoglibrary

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.performancemeasurement.Lottie.ClickListenerCustom

class LottieAlertDialogCustom : AlertDialog
{
    private var mContext : Context
    private var mType:Int?=null
    private var customAnimationFileName:String?=null
    private var mTitle:String? = null
    private var mDescription:String? = null
    private var mPositiveText:String? = null
    private var mNegativeText:String? = null
    private var mNoneText:String? = null
    private var mPositiveListener : ClickListenerCustom? = null
    private var mNegativeListener : ClickListenerCustom? = null
    private var mNoneListener : ClickListenerCustom? = null

    private lateinit var lAnimation : LottieAnimationView
    private lateinit var tvTitle : TextView
    private lateinit var tvDescription : TextView
    private lateinit var btnPositive : Button
    private lateinit var btnNegative : Button
    private lateinit var btnNone : Button
    private var positiveBtnColor:Int? = null
    private var positiveTextColor:Int? = null
    private var negativeBtnColor:Int? = null
    private var negativeTextColor:Int? = null
    private var noneBtnColor:Int? = null
    private var noneTextColor:Int? = null
    private lateinit var animationFadeIn : Animation
    private lateinit var animationFadeOut: Animation

    private constructor(
            context: Context,
            type: Int?,
            customName: String?,
            title: String?,
            description: String?,
            positiveText: String?,
            negativeText: String?,
            noneText: String?,
            positiveListener: ClickListenerCustom?,
            negativeListener: ClickListenerCustom?,
            noneListener: ClickListenerCustom?,
            positiveBtnColor : Int?,
            positiveTextColor : Int?,
            negativeBtnColor : Int?,
            negativeTextColor : Int?,
            noneBtnColor : Int?,
            noneTextColor : Int?
    ) : super(context) {
        this.mContext = context
        this.mType = type
        this.customAnimationFileName = customName
        this.mTitle = title
        this.mDescription = description
        this.mPositiveText = positiveText
        this.mNegativeText = negativeText
        this.mNoneText = noneText
        this.mPositiveListener = positiveListener
        this.mNegativeListener = negativeListener
        this.mNoneListener = noneListener
        this.positiveBtnColor = positiveBtnColor
        this.positiveTextColor = positiveTextColor
        this.negativeBtnColor = negativeBtnColor
        this.negativeTextColor = negativeTextColor
        this.noneBtnColor = noneBtnColor
        this.noneTextColor = noneTextColor
        animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        animationFadeIn.duration = 50
        animationFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animationFadeOut.duration = 50
    }


    data class Builder(
            private val context: Context? = null,
            private val type:Int? = null,
            private val customName: String? = null
    )
    {
        private var title:String? = null
        private var description:String? = null
        private var positiveText:String? = null
        private var negativeText:String? = null
        private var noneText:String? = null
        private var positiveListener:ClickListenerCustom? = null
        private var negativeListener:ClickListenerCustom? = null
        private var noneListener:ClickListenerCustom? = null
        private var positiveBtnColor:Int? = null
        private var positiveTextColor:Int? = null
        private var negativeBtnColor:Int? = null
        private var negativeTextColor:Int? = null
        private var noneBtnColor:Int? = null
        private var noneTextColor:Int? = null

        fun setTitle(title:String?) : Builder = apply { this.title = title ; return@apply}
        fun setDescription(description:String?) : Builder= apply { this.description = description ; return@apply}
        fun setPositiveText(positiveText:String?) : Builder= apply { this.positiveText = positiveText ; return@apply}
        fun setNegativeText(negativeText:String?) : Builder= apply { this.negativeText = negativeText ; return@apply}
        fun setNoneText(noneText:String?) : Builder= apply { this.noneText = noneText ; return@apply}
        fun setPositiveListener(positiveListener:ClickListenerCustom?) : Builder= apply { this.positiveListener = positiveListener ; return@apply}
        fun setNegativeListener(negativeListener:ClickListenerCustom?) : Builder= apply { this.negativeListener = negativeListener ; return@apply}
        fun setNoneListener(noneListener:ClickListenerCustom?): Builder = apply { this.noneListener = noneListener ; return@apply}
        fun setPositiveButtonColor(color:Int?) : Builder= apply { this.positiveBtnColor = color ; return@apply}
        fun setPositiveTextColor(color:Int?) : Builder= apply { this.positiveTextColor = color ; return@apply}
        fun setNegativeButtonColor(color:Int?) : Builder= apply { this.negativeBtnColor = color ; return@apply}
        fun setNegativeTextColor(color:Int?) : Builder= apply { this.negativeTextColor = color ; return@apply}
        fun setNoneButtonColor(color:Int?) : Builder= apply { this.noneBtnColor = color ; return@apply}
        fun setNoneTextColor(color:Int?) : Builder= apply { this.noneTextColor = color ; return@apply}
        fun build() = LottieAlertDialogCustom(context!!,type,customName,title,description,positiveText,
                negativeText,noneText,positiveListener,negativeListener,noneListener,
                positiveBtnColor,positiveTextColor,negativeBtnColor,negativeTextColor,noneBtnColor,noneTextColor)

        fun getContext() : Context? {return context}
        fun getType() : Int? { return  type}
        fun getCustomName() : String? { return customName}
        fun getTitle() : String? { return  title}
        fun getDescription() : String? { return  description}
        fun getPositiveText() : String? { return  positiveText}
        fun getNegativeText() : String? { return  negativeText}
        fun getNoneText() : String? { return  noneText}
        fun getPositiveListener() : ClickListenerCustom? { return  positiveListener}
        fun getNegativeListener() : ClickListenerCustom? { return  negativeListener}
        fun getNoneListener() : ClickListenerCustom? { return  noneListener}
        fun getPositiveButtonColor() : Int? { return  positiveBtnColor}
        fun getPositiveTextColor() : Int? { return  positiveTextColor}
        fun getNegativeButtonColor() : Int? { return  negativeBtnColor}
        fun getNegativeTextColor() : Int? { return  negativeTextColor}
        fun getNoneButtonColor() : Int? { return  noneBtnColor}
        fun getNoneTextColor() : Int? { return  noneTextColor}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_alert_dialog)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        findView()
    }

    private fun findView()
    {
        lAnimation=findViewById(R.id.lAnimation)
        tvTitle=findViewById(R.id.tvTitle)
        tvDescription=findViewById(R.id.tvDescription)
        btnPositive=findViewById(R.id.btnPositive)
        btnNegative=findViewById(R.id.btnNegative)
        btnNone=findViewById(R.id.btnNone)
        setView()
    }

    private fun setView()
    {
        // SET VIEW
        lAnimation.startAnimation(animationFadeIn)
        if (mTitle!=null)
        {
            tvTitle.setText(mTitle)
            tvTitle.visibility=View.VISIBLE
        }
        else
        {
            tvTitle.visibility=View.GONE
        }
        if (mDescription!=null)
        {
            tvDescription.setText(mDescription)
            tvDescription.gravity = View.TEXT_ALIGNMENT_TEXT_START
            tvDescription.visibility=View.VISIBLE
        }
        else
        {
            tvDescription.visibility=View.GONE
        }
        if (mPositiveText!=null)
        {
            btnPositive.setText(mPositiveText)
            btnPositive.visibility=View.VISIBLE
            btnPositive.setOnClickListener(View.OnClickListener { mPositiveListener?.onClick(LottieAlertDialogCustom@this) })
        }
        else
        {
            btnPositive.visibility=View.GONE
        }
        if (mNegativeText!=null)
        {
            btnNegative.setText(mNegativeText)
            btnNegative.visibility=View.VISIBLE
            btnNegative.setOnClickListener(View.OnClickListener { mNegativeListener?.onClick(LottieAlertDialogCustom@this) })
        }
        else
        {
            btnNegative.visibility=View.GONE
        }
        if (mNoneText!=null)
        {
            btnNone.setText(mNoneText)
            btnNone.visibility=View.VISIBLE
            btnNone.setOnClickListener(View.OnClickListener { mNoneListener?.onClick(LottieAlertDialogCustom@this) })
        }
        else
        {
            btnNone.visibility=View.GONE
        }

        if (positiveBtnColor!=null)
            btnPositive.setBackgroundColor(positiveBtnColor!!)
        else
            btnPositive.background.clearColorFilter()
        if (positiveTextColor!=null)
            btnPositive.setTextColor(positiveTextColor!!)
        else
            btnPositive.setTextColor(Color.parseColor("#000000"))
        if (negativeBtnColor!=null)
            btnNegative.setBackgroundColor(negativeBtnColor!!)
        else
            btnNegative.background.clearColorFilter()
        if (negativeTextColor!=null)
            btnNegative.setTextColor(negativeTextColor!!)
        else
            btnNegative.setTextColor(Color.parseColor("#000000"))
        if (noneBtnColor!=null)
            btnNone.setBackgroundColor(noneBtnColor!!)
        else
            btnNone.background.clearColorFilter()
        if (noneTextColor!=null)
            btnNone.setTextColor(noneTextColor!!)
        else
            btnNone.setTextColor(Color.parseColor("#000000"))

        // TYPE CONTROL
        lAnimation.repeatCount=0
        if (mType == DialogTypes.TYPE_LOADING)
        {
            lAnimation.setAnimation("loading.json")
            lAnimation.repeatCount=LottieDrawable.INFINITE
        }
        else if (mType == DialogTypes.TYPE_SUCCESS)
        {
            lAnimation.setAnimation("success.json")
        }
        else if (mType == DialogTypes.TYPE_ERROR)
        {
            lAnimation.setAnimation("error.json")
        }
        else if (mType == DialogTypes.TYPE_WARNING)
        {
            lAnimation.setAnimation("warning.json")
        }
        else if (mType == DialogTypes.TYPE_QUESTION)
        {
            lAnimation.setAnimation("question.json")
        }
        else if (mType == DialogTypes.TYPE_CUSTOM)
        {
            lAnimation.setAnimation(customAnimationFileName)
        }
        lAnimation.playAnimation()
    }

    fun changeDialog(builder : Builder)
    {
        mContext= builder.getContext()!!
        mType=builder.getType()
        mTitle=builder.getTitle()
        mDescription=builder.getDescription()
        mPositiveText=builder.getPositiveText()
        mNegativeText=builder.getNegativeText()
        mNoneText=builder.getNoneText()
        mPositiveListener=builder.getPositiveListener()
        mNegativeListener=builder.getNegativeListener()
        mNoneListener=builder.getNoneListener()
        positiveBtnColor=builder.getPositiveButtonColor()
        positiveTextColor=builder.getPositiveTextColor()
        negativeBtnColor=builder.getNegativeButtonColor()
        negativeTextColor=builder.getNegativeTextColor()
        noneBtnColor=builder.getNoneButtonColor()
        noneTextColor=builder.getNoneTextColor()
        lAnimation.startAnimation(animationFadeOut)
        Handler().postDelayed(Runnable { setView() },50)
    }
}