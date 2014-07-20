package com.mick.bincalc;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;

public class MainActivity extends Activity implements OnItemSelectedListener, View.OnClickListener, ColorPicker.OnColorChangedListener
{
    public static final int
            DATA_TYPE_DECIMAL = 10,
            DATA_TYPE_HEXADECIMAL = 16,
            DATA_TYPE_BINARY = 2,
            DATA_TYPE_NONE = 0,
            DATA_TYPE_ASCII = -1,
            DATA_TYPE_IP_ADDRESS = -2,
            DATA_TYPE_ANY_BASE = -3,
            DATA_TYPE_COLOR = -4;
    
	EditText decView;
    EditText hexView;
    EditText binView;
    EditText ipView;
    EditText charView;
    EditText anyBaseView;
    TextView colorView;
	Spinner anyBaseSpinner;
	TextView bitNumber;
	Number number=new Number(0);	
	
	final int 
			defaultBase=8,
			defaultBaseSelection=defaultBase-Number.minBase;	
	int selectedBase = defaultBase;

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        selectedBase = position + Number.minBase;

        converting=true;
        setTextKeepSelection(anyBaseView, number.toAnyBase(selectedBase));
        converting=false;
    }

    public void onNothingSelected(AdapterView<?> parent)
    {

    }


	boolean converting = false;

	public void convertNumber(String numberStr, int type)
	{
		if (converting)
		{
			return;
		}		
		converting = true;

		if (numberStr.length() == 0)
		{
			number = new Number(0);
		}
		else
		{
			switch (type)
			{
				case DATA_TYPE_IP_ADDRESS:
					number.fromIpAddress(numberStr);
					break;
				
				case DATA_TYPE_NONE: //this must fall through to dec
                case DATA_TYPE_COLOR:
				case DATA_TYPE_DECIMAL:
					number = new Number(numberStr, DATA_TYPE_DECIMAL);
					break;
	
				case DATA_TYPE_HEXADECIMAL:
					number = new Number(numberStr, DATA_TYPE_HEXADECIMAL);
					break;
	
				case DATA_TYPE_BINARY:
					number = new Number(numberStr, DATA_TYPE_BINARY);
					break;
					
				case DATA_TYPE_ASCII:
					number = new Number();
					number.fromChar(numberStr);
					break;
					
				case DATA_TYPE_ANY_BASE:
					number = new Number(numberStr, selectedBase);
					break;
			}
		}
		
		bitNumber.setText(String.format("%d bits", number.getBitSize()));
		
		if (type != DATA_TYPE_DECIMAL)
		{
			decView.setText(number.toAnyBase(DATA_TYPE_DECIMAL));
		}
		 

		if (type != DATA_TYPE_HEXADECIMAL || (number.isInputCorrect() == false && number.getOriginalBase() == DATA_TYPE_HEXADECIMAL))
		{
			setTextKeepSelection(hexView, number.toAnyBase(DATA_TYPE_HEXADECIMAL));
		}
		
		if (type != DATA_TYPE_IP_ADDRESS)
		{
			ipView.setText(number.toIpAddress());
		}
		
		if (type != DATA_TYPE_BINARY || (number.isInputCorrect() == false && number.getOriginalBase() == DATA_TYPE_BINARY))
		{
			setTextKeepSelection(binView, number.toAnyBase(DATA_TYPE_BINARY));
		}
		
		if (type != DATA_TYPE_ASCII)
		{
			
			if (number.toChar() == null)
			{
				setTextKeepSelection(charView, "");
				if (number.getCharDescription() == null) charView.setHint(R.string.ascii_hint);
				else charView.setHint(number.getCharDescription());
			}
			else 
			{
				setTextKeepSelection(charView, number.toChar());
				charView.setHint(R.string.ascii_hint);
			}
		}
		
		if (type != DATA_TYPE_ANY_BASE || (number.isInputCorrect() == false && number.getOriginalBase() == selectedBase))
		{
			setTextKeepSelection(anyBaseView, number.toAnyBase(selectedBase));
		}
		
		int color = (int) (0xFF000000 | (0xFFFFFF & number.toDecimal()));
		Number colorn = new Number(color & 0xFFFFFF);
		String colorHex = colorn.toAnyBase(DATA_TYPE_HEXADECIMAL);
		while (colorHex.length() < 6) colorHex = "0"+colorHex;
		colorView.setText('#'+colorHex);
		colorView.setBackgroundColor(color);
        colorView.setTextColor(0xffffff ^ color);
		
		converting = false;
	}
	
	void setTextKeepSelection(EditText edit, String newText)
	{
		int //start = edit.getSelectionStart(),
				end = edit.getSelectionEnd();
		edit.setText(newText);
		if (number.isZero()) edit.selectAll();
		else edit.setSelection(Math.min((end>0)?end-1:end, newText.length()));
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Input/Output
		decView = (EditText) findViewById(R.id.decimalEdit);
		hexView = (EditText) findViewById(R.id.hexEdit);
		binView = (EditText) findViewById(R.id.binEdit);
		ipView = (EditText) findViewById(R.id.ipEdit);
		charView = (EditText) findViewById(R.id.charEdit);
		anyBaseView = (EditText) findViewById(R.id.baseEdit);
		anyBaseSpinner = (Spinner) findViewById(R.id.baseSpinner);
		colorView = (TextView) findViewById(R.id.colorEdit);

        colorView.setOnClickListener(this);
		
		ArrayList<String> bases = new ArrayList<String>();
		for (int i=Number.minBase; i <= Number.maxBase; i++)
		{
			bases.add("base "+i);
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bases);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		anyBaseSpinner.setAdapter(dataAdapter);
		anyBaseSpinner.setSelection(defaultBaseSelection);
		
		anyBaseSpinner.setOnItemSelectedListener(this);
		
		
		//Output number of bits
		bitNumber = (TextView) findViewById(R.id.bitNumber);		
		
		convertNumber(number.toAnyBase(10), DATA_TYPE_NONE);
		decView.selectAll();
		
		charView.addTextChangedListener(new TextEditListener(DATA_TYPE_ASCII, this)
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                super.afterTextChanged(s);
                charView.selectAll();
            }
        });

		decView.addTextChangedListener(new TextEditListener(DATA_TYPE_DECIMAL, this));
		
		ipView.addTextChangedListener(new TextEditListener(DATA_TYPE_IP_ADDRESS, this));

		hexView.addTextChangedListener(new TextEditListener(DATA_TYPE_HEXADECIMAL, this));

		binView.addTextChangedListener(new TextEditListener(DATA_TYPE_BINARY, this));
		
		anyBaseView.addTextChangedListener(new TextEditListener(DATA_TYPE_ANY_BASE, this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menuReset:
				convertNumber("0", DATA_TYPE_NONE);
				return true;
				
			case R.id.menuIncrement:
				Long li = Long.valueOf(number.toDecimal() + 1);
				convertNumber(li.toString(), DATA_TYPE_NONE);
				return true;
				
			case R.id.menuDecrement:
				Long ld = Long.valueOf(number.toDecimal());
				if (ld > 0) ld--;
				convertNumber(ld.toString(), DATA_TYPE_NONE);
				return true;				
				
			case R.id.menuShiftLeft:
				Long lsl = Long.valueOf(number.toDecimal() << 1);
				convertNumber(lsl.toString(), DATA_TYPE_NONE);
				return true;
				
			case R.id.menuShiftRight:
				Long lsr = Long.valueOf(number.toDecimal() >> 1);
				convertNumber(lsr.toString(), DATA_TYPE_NONE);
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.colorEdit:
                int col = (int) number.toDecimal() | 0xff000000;
                
                View view = LayoutInflater.from(this).inflate(R.layout.color_picker, null);
                ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.colorPicker);
                colorPicker.setOnColorChangedListener(this);
                SVBar svBar = (SVBar) view.findViewById(R.id.svbar);
                colorPicker.addSVBar(svBar);
                svBar.setColorPicker(colorPicker);

                colorPicker.setColor(col);
                colorPicker.setShowOldCenterColor(false);

                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("Done", null)
                        .show();
                break;
        }
    }

    public void onColorChanged(int i)
    {
        i &= 0xffffff;
        convertNumber(String.valueOf(i), DATA_TYPE_COLOR);
    }
}
