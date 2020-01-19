package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MESSAGE_ID = "Holding score";
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private TextView scoreBoardTextView;

    private Button trueButton;
    private Button falseButton;

    private ImageButton nextButton;
    private ImageButton backButton;

    private int currentQuestionIndex = 0;
    private boolean holdMe;
    private int currentScore = 0;
    private int temp = 0;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.questionReader);
        questionCounterTextView = findViewById(R.id.counter_text);
        scoreBoardTextView = findViewById(R.id.score);

        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);

        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


       questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
           @Override
           public void procressFinshed(ArrayList<Question> questionAraayList) {

               questionTextView.setText(questionAraayList.get(currentQuestionIndex).getAnswer());
               questionCounterTextView.setText((currentQuestionIndex + 1) + " / " + questionList.size());
               Log.d("Inside", "procressFinished: " + questionAraayList);
           }
       });

    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.nextButton:
                updateQuestion(1);
                temp = 0;
                break;
            case R.id.backButton:
                updateQuestion(0);
                break;
            case R.id.trueButton:
                checkAnswer(true);
                break;
            case R.id.falseButton:
                checkAnswer(false);
                break;
        }

    }
    private void checkAnswer(boolean userCorrect)
    {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        if (userCorrect == answerIsTrue)
        {
            if(temp == 0)
            {
                currentScore++;
            }
            temp++;
            toastMe("Correct!");
            fadeAnimation();
        }
        else
        {
            temp = 1;
            toastMe("Incorrect!");
            shakeAnimation();
        }
        int scoreFromDisk = currentScore;
        SharedPreferences saveScore = getSharedPreferences(MESSAGE_ID,MODE_PRIVATE);
        SharedPreferences.Editor editor = saveScore.edit();
        editor.putInt("Score", scoreFromDisk);
        editor.apply();

       scoreFromDisk = saveScore.getInt("Score",scoreFromDisk);
        scoreBoardTextView.setText("Score: " + scoreFromDisk + " correct questions");

    }
    private void updateQuestion(int value)
    {

        if(value == 1)
        {
            currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        }
        if(value == 0)
        {
            if(currentQuestionIndex == 0)
            {
                currentQuestionIndex = questionList.size() - 1;
            }
            else
            currentQuestionIndex = (currentQuestionIndex - 1) %questionList.size();
        }
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex + 1 +  " / " + questionList.size());


    }
    private void fadeAnimation()
    {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation((alphaAnimation));

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {
                cardView.setCardBackgroundColor(Color.argb(100,63,96,155));
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                cardView.setCardBackgroundColor(Color.argb(100,83,81,81));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void shakeAnimation()
    {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);

        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
       shake.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation)
           {
               cardView.setCardBackgroundColor(Color.argb(100,163,54,54));
           }

           @Override
           public void onAnimationEnd(Animation animation)
           {
               cardView.setCardBackgroundColor(Color.argb(100,83,81,81));
           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });
    }

    private void toastMe(String toastMessage)
    {
        Toast toast = new Toast(MainActivity.this);
        toast.cancel();
        toast.makeText(this,toastMessage,Toast.LENGTH_SHORT).show();
    }
}
