package com.mick.bincalc;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MainActivity extends Activity
{
	EditText decView, hexView, binView, ipView, charView, anyBaseView, colorView;
	Spinner anyBaseSpinner;
	TextView bitNumber;
	Number number=new Number(0);	
	
	final int 
			defaultBase=8,
			defaultBaseSelection=defaultBase-Number.minBase;	
	int selectedBase = defaultBase;

	public static enum DataType
	{
		Decimal, Hexadecimal, Binary, none, Ascii, IpAddress, AnyBase
	};

	boolean converting = false;
	
	void showMessage(String msg)
	{
		Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
		t.show();
	}

	public void convertNumber(String numberStr, DataType type)
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
				case IpAddress:
					number.fromIpAddress(numberStr);
					break;
				
				case none: //this must fall through to dec
				case Decimal:
					number = new Number(numberStr, 10);
					break;
	
				case Hexadecimal:
					number = new Number(numberStr, 16);				
					break;
	
				case Binary:
					number = new Number(numberStr, 2);					 
					break;
					
				case Ascii:
					number = new Number();
					number.fromChar(numberStr);
					break;
					
				case AnyBase:
					number = new Number(numberStr, selectedBase);
					break;
			}
		}
		
		bitNumber.setText(String.format("%d bits", number.getBitSize()));
		
		if (type != DataType.Decimal)
		{
			decView.setText(number.toAnyBase(10));
		}
		 

		if (type != DataType.Hexadecimal || (number.isInputCorrect() == false && number.getOriginalBase() == 16))
		{
			setTextKeepSelection(hexView, number.toAnyBase(16));
		}
		
		if (type != DataType.IpAddress)
		{
			ipView.setText(number.toIpAddress());
		}
		
		if (type != DataType.Binary || (number.isInputCorrect() == false && number.getOriginalBase() == 2))
		{
			setTextKeepSelection(binView, number.toAnyBase(2));			
		}
		
		if (type != DataType.Ascii)
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
		
		if (type != DataType.AnyBase || (number.isInputCorrect() == false && number.getOriginalBase() == selectedBase))
		{
			setTextKeepSelection(anyBaseView, number.toAnyBase(selectedBase));
		}
		
		int color = (int) (0xFF000000 | (0xFFFFFF & number.toDecimal()));
		Number colorn = new Number(color & 0xFFFFFF);
		String colorHex = colorn.toAnyBase(16);
		while (colorHex.length() < 6) colorHex = "0"+colorHex;
		colorView.setText('#'+colorHex);
		colorView.setBackgroundColor(color);
		
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
		
		 try {
		        ViewConfiguration config = ViewConfiguration.get(this);
		        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
		        if(menuKeyField != null) {
		            menuKeyField.setAccessible(true);
		            menuKeyField.setBoolean(config, false);
		        }
		    } catch (Exception ex) {
		        // Ignore
		    }


		//Input/Output
		decView = (EditText) findViewById(R.id.decimalEdit);
		hexView = (EditText) findViewById(R.id.hexEdit);
		binView = (EditText) findViewById(R.id.binEdit);
		ipView = (EditText) findViewById(R.id.ipEdit);
		charView = (EditText) findViewById(R.id.charEdit);
		anyBaseView = (EditText) findViewById(R.id.baseEdit);
		anyBaseSpinner = (Spinner) findViewById(R.id.baseSpinner);
		colorView = (EditText) findViewById(R.id.colorEdit);
		
		ArrayList<String> bases = new ArrayList<String>();
		for (int i=Number.minBase; i <= Number.maxBase; i++)
		{
			bases.add("base "+i);
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bases);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		anyBaseSpinner.setAdapter(dataAdapter);
		anyBaseSpinner.setSelection(defaultBaseSelection);
		
		anyBaseSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectedBase = arg2 + Number.minBase;
				
				converting=true;
				setTextKeepSelection(anyBaseView, number.toAnyBase(selectedBase));
				converting=false;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
		//Output number of bits
		bitNumber = (TextView) findViewById(R.id.bitNumber);		
		
		convertNumber(number.toAnyBase(10), DataType.none);
		decView.selectAll();
		
		charView.addTextChangedListener(new TextEditListener(DataType.Ascii, this)
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                super.afterTextChanged(s);
                charView.selectAll();
            }
        });

		decView.addTextChangedListener(new TextEditListener(DataType.Decimal, this));
		
		ipView.addTextChangedListener(new TextEditListener(DataType.IpAddress, this));

		hexView.addTextChangedListener(new TextEditListener(DataType.Hexadecimal, this));

		binView.addTextChangedListener(new TextEditListener(DataType.Binary, this));
		
		anyBaseView.addTextChangedListener(new TextEditListener(DataType.AnyBase, this));
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
				convertNumber("0", DataType.none);	
				return true;
				
			case R.id.menuIncrement:
				Long li = Long.valueOf(number.toDecimal() + 1);
				convertNumber(li.toString(), DataType.none);	
				return true;
				
			case R.id.menuDecrement:
				Long ld = Long.valueOf(number.toDecimal());
				if (ld > 0) ld--;
				convertNumber(ld.toString(), DataType.none);	
				return true;				
				
			case R.id.menuShiftLeft:
				Long lsl = Long.valueOf(number.toDecimal() << 1);
				convertNumber(lsl.toString(), DataType.none);	
				return true;
				
			case R.id.menuShiftRight:
				Long lsr = Long.valueOf(number.toDecimal() >> 1);
				convertNumber(lsr.toString(), DataType.none);	
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
