package br.com.globalcode.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String EMPTY_VALUE = "";
	private static final int DIALOG_OPERATION_TYPE = 0;
	private static final int DIALOG_REAL_ESTATE_TYPE = 1;
	private static final int DIALOG_INVALID_VALUES = 2;
	
	private static final String REAL_STATE_TYPE = "real_state_type";
	private static final String OPERATION_TYPE = "operation_type";
	private static final String CITY_VALUE = "city_value";
	
	private static final CharSequence[] arrayOperationType = new CharSequence[] { "Aluguel", "Venda", "Temporada" };
	private static final CharSequence[] arrayRealEstateType = new CharSequence[] { "Casa", "Apartamento", "Sala", "Salão", "Chácara" };
	private static final boolean[] arrayBooleanRealEstateType = new boolean[] { false, false, false, false, false };

	private EditText cityEditText;
	private TextView operationTypeTextView;
	private TextView realEstateTypeTextView;

	private TextView minTextView;
	private TextView maxTextView;
	private SeekBar minSeekBar;
	private SeekBar maxSeekBar;

	private Button searchButton;
	
	private Spinner spinnerState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bindElements();
		bindListeners();
		repairSavedInstanceStateValues(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putCharSequence(OPERATION_TYPE, this.operationTypeTextView.getText());
		outState.putCharSequence(REAL_STATE_TYPE, this.realEstateTypeTextView.getText());
		outState.putCharSequence(CITY_VALUE, this.cityEditText.getText());
	}

	private void repairSavedInstanceStateValues(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			this.operationTypeTextView.setText(savedInstanceState.getCharSequence(OPERATION_TYPE));
			this.realEstateTypeTextView.setText(savedInstanceState.getCharSequence(REAL_STATE_TYPE));
			this.cityEditText.setText(savedInstanceState.getCharSequence(CITY_VALUE));
		}
	}

	private void bindElements() {

		this.operationTypeTextView = (TextView) this.findViewById(R.id.textViewOperationType);
		this.realEstateTypeTextView = (TextView) this.findViewById(R.id.textViewRealStateType);
		this.cityEditText = (EditText) this.findViewById(R.id.editTextCity);
		this.minTextView = (TextView) this.findViewById(R.id.textViewMinValue);
		this.maxTextView = (TextView) this.findViewById(R.id.textViewMaxValue);
		this.minSeekBar = (SeekBar) this.findViewById(R.id.seekBarMinValue);
		this.maxSeekBar = (SeekBar) this.findViewById(R.id.seekBarMaxValue);
		this.searchButton = (Button) this.findViewById(R.id.buttonSearch);
		this.spinnerState = (Spinner) this.findViewById(R.id.spinnerState);
	}

	private void bindListeners() {
		
		this.minSeekBar.setOnSeekBarChangeListener(listenerSeekBar);
		this.maxSeekBar.setOnSeekBarChangeListener(listenerSeekBar);
		this.searchButton.setOnClickListener(listenerOnClick);
	}
	
	public void showOperationTypeDialog(View view) {
		showDialog( DIALOG_OPERATION_TYPE );
	}

	public void showRealStateTypeDialog(View View) {
		showDialog( DIALOG_REAL_ESTATE_TYPE );
	}
	
	private void showInvalidValuesDialog() {
		removeDialog( DIALOG_INVALID_VALUES );
		showDialog( DIALOG_INVALID_VALUES );
	}
	
	protected Dialog onCreateDialog(int idDialogType) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch (idDialogType) {
		
		case DIALOG_REAL_ESTATE_TYPE:

			builder.setTitle("Tipo do Imóvel");
			builder.setMultiChoiceItems(arrayRealEstateType, arrayBooleanRealEstateType,
					new DialogInterface.OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							
							arrayBooleanRealEstateType[which] = isChecked;
							String viewText = EMPTY_VALUE;
							for (int i = 0; i < arrayBooleanRealEstateType.length; i++) {
								if (arrayBooleanRealEstateType[i]) {
									if (viewText.length() == 0) {
										viewText += arrayRealEstateType[i];
									} else {
										viewText += ", " + arrayRealEstateType[i];
									}
								}
							}
							
							if(EMPTY_VALUE.equalsIgnoreCase(viewText)) {
								viewText = "Tipo do Imóvel";
							}
							realEstateTypeTextView.setText(viewText);
						}
					});
			
			builder.setPositiveButton("Ok", null);
			return builder.create();
			
		case DIALOG_OPERATION_TYPE:

			builder.setTitle("Tipo da Operação");
			builder.setSingleChoiceItems(arrayOperationType, 0,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							operationTypeTextView.setText(arrayOperationType[which]);
						}
					});
			builder.setPositiveButton("Ok", null);
			return builder.create();
			
		case DIALOG_INVALID_VALUES: 
			
			String inputField = "";
			String message = "Para continuar, por favor, informe um valor para ";
			
			if(isCityValueInvalid()) {
				inputField = "Cidade";
				message += inputField;
			} else if(isStateValueInvalid()) {
				inputField = "Estado";
				message += inputField;
			} else if(isOperationValueInvalid()) {
				inputField = "Operação";
				message += inputField;
			} else if(isRealStateValueInvalid()) {
				inputField = "Tipo de imóvel";
				message += inputField;
			} else if(isMinValueInvalid()) {
				inputField = "Valor mínimo";
				message += inputField;
			} else if(isMaxValueInvalid()) {
				inputField = "Valor máximo";
				message += inputField;
			} else if(isMinValueGreaterThanMaxValue()) {
				message = "O valor Mínimo não pode ser maior que o valor Máximo";
			}
			
			builder.setTitle("Aviso Importante");
			builder.setMessage(message);
			builder.setPositiveButton("Ok", null);
			return builder.create();
			
		default:
			return super.onCreateDialog(idDialogType);
		}
	}
	
	private OnSeekBarChangeListener listenerSeekBar = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			if (seekBar.getId() == R.id.seekBarMinValue) {

				minTextView.setText("Valor Mínimo: " + progress);
			} else {

				if (progress < minSeekBar.getProgress()) {
					minSeekBar.setProgress(progress - 1);
				}

				maxTextView.setText("Valor Máximo: " + progress);
			}
		}
	};

	private OnClickListener listenerOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if(validateInputValues()) {
				showInvalidValuesDialog();
			}			
		}
	};
	
	private boolean validateInputValues() {
		return isCityValueInvalid() 
				|| isOperationValueInvalid() 
					|| isRealStateValueInvalid()
						|| isMinValueInvalid()
							|| isMaxValueInvalid()
								|| isStateValueInvalid()
									|| isMinValueGreaterThanMaxValue();
	}		
	
	private boolean isCityValueInvalid() {
		return EMPTY_VALUE.equals(cityEditText.getText().toString());
	}
	
	private boolean isOperationValueInvalid() {
		return "Operação".equalsIgnoreCase(operationTypeTextView.getText().toString())
					|| EMPTY_VALUE.equalsIgnoreCase(operationTypeTextView.getText().toString());
	}
	
	private boolean isRealStateValueInvalid() {
		return "Tipo de Imóvel".equalsIgnoreCase(realEstateTypeTextView.getText().toString())
					|| EMPTY_VALUE.equalsIgnoreCase(realEstateTypeTextView.getText().toString());
	}
	
	private boolean isMinValueInvalid() {
		return "Valor Mínimo:".equalsIgnoreCase(minTextView.getText().toString());
	}

	private boolean isMaxValueInvalid() {
		return "Valor Máximo:".equalsIgnoreCase(maxTextView.getText().toString());
	}
	
	private boolean isStateValueInvalid() {
		return "Selecione".equalsIgnoreCase(spinnerState.getSelectedItem().toString());
	}
	
	private boolean isMinValueGreaterThanMaxValue() {
		return minSeekBar.getProgress() > maxSeekBar.getProgress();
	}

}
