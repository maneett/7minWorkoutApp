package com.example.a7min_workout

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7min_workout.databinding.ActivityExerciseBinding
import java.util.*
import kotlin.collections.ArrayList
class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding:ActivityExerciseBinding? = null
    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer : CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var exerciseAdapter: ExerciseStatusAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        setupRestView()
        setupStatusRecyclerView()
    }

    private fun setupStatusRecyclerView(){
         binding?.rvExerciseStatus?.layoutManager =
              LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView(){

        try {
            val soundURI = Uri.parse("android.resource://com.example.a7min_workout/"+R.raw.press)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpCommingExerciseName?.visibility = View.VISIBLE
        binding?.tvUpCommingLabel?.visibility = View.VISIBLE
        if(restTimer!=null){
            restTimer?.cancel()
            restProgress= 0
        }
        binding?.tvUpCommingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }
    private fun setUpExercise(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
//        binding?.tvTimerExercise?.text = "Exercise Name"
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpCommingExerciseName?.visibility = View.INVISIBLE
        binding?.tvUpCommingLabel?.visibility = View.INVISIBLE


        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()
    }

     private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object: CountDownTimer(10000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setUpExercise()
            }
        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        restTimer = object: CountDownTimer(30000,1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                if(exerciseProgress > 30){
                    exerciseProgress = 0
                }
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }
            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseAdapter!!.notifyDataSetChanged()

            if(currentExercisePosition < exerciseList!!.size-1){
                setupRestView()
                }else{
                    Toast.makeText(this@ExerciseActivity,"Congratulations! You have " +
                            "completed the 7 minutes workout.",Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }
        if(player != null){
            player!!.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
       if(status == TextToSpeech.SUCCESS){
           val result = tts?.setLanguage(Locale.US)
           if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
               Log.e("tts","Language Not Supported")
           }else{
               Log.e("tts","Not Initialize")
           }
       }
    }
    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
    }
}