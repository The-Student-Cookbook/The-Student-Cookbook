package cs506.studentcookbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cs506.studentcookbook.R;
import cs506.studentcookbook.database.DBTools;
import cs506.studentcookbook.model.Preferences;
import cs506.studentcookbook.utils.RecipeListViewAdapter;

public class PersistentDataQueryActivity extends Activity {

    private List<String> selectedRecipes;
    private View selectedRecipeView = null;
    private DBTools dbTools;
    private int cost = 0;
    private String cookingTime;
    private String prepTime;
    private String groupSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistent_data_query);

        Button cancelButton = (Button) findViewById(R.id.persistent_data_cancelbutton);
        Button nextButton = (Button) findViewById(R.id.persistent_data_nextbutton);
        final EditText cookingTimeEditText = (EditText) findViewById(R.id.cookingTime_textinput);
        final EditText prepTimeEditText = (EditText) findViewById(R.id.prepTime_textinput);
        final EditText groupSizeEditText = (EditText) findViewById(R.id.groupSize_textinput);



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookingTime = cookingTimeEditText.getText().toString();
                prepTime = prepTimeEditText.getText().toString();
                groupSize = groupSizeEditText.getText().toString();

                if(cost == 0 || cookingTime == null || prepTime == null || groupSize == null)
                    Toast.makeText(PersistentDataQueryActivity.this, "Please fill in all of the information!", Toast.LENGTH_LONG).show();
                else{
                    Intent goToNextMenu = new Intent(PersistentDataQueryActivity.this, CuisineQuery.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cookTime", cookingTime);
                    bundle.putString("prepTime", prepTime);
                    bundle.putString("groupSize", groupSize);
                    bundle.putInt("cost", cost);
                    goToNextMenu.putExtras(bundle);
                    PersistentDataQueryActivity.this.startActivity(goToNextMenu);
                }
            }
        });


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.costradio_1:
                if (checked)
                    cost = 1;
                break;
            case R.id.costradio_2:
                if (checked)
                    cost = 2;
                break;
            case R.id.costradio_3:
                if (checked)
                    cost = 3;
                break;
            case R.id.costradio_4:
                if (checked)
                    cost = 4;
                break;
        }
    }

}
