package com.mick.bincalc;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

/**
 * Created by michal on 20/07/14.
 */
public class TextEditListener implements TextWatcher
{
    private final int dataType;
    private final MainActivity mainActivity;

    public TextEditListener(int dataType, MainActivity mainActivity)
    {
        this.dataType = dataType;
        this.mainActivity = mainActivity;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    public void afterTextChanged(Editable s)
    {
        mainActivity.convertNumber(s.toString(), this.dataType);
    }
}
