package com.RSen.MatrixMate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import Jama.Matrix;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MatrixMateActivity extends SherlockActivity {
	/** Called when the activity is first created. */
	private int rows = 1;
	private int columns = 1;
	private char currentMatrix = 'A';
	char[] matrices;

	Boolean complexInput = false;
	LinkedHashMap<Integer, String> complexStrings = new LinkedHashMap<Integer, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		MainSearchLayout searchLayout = new MainSearchLayout(this, null);

		setContentView(searchLayout);
		currentMatrix = getIntent().getCharExtra("currentMatrix", 'A');
		
			matrices = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
		
		loadData();
		setFocusOrder();
		setOnClickListeners();
		getSupportActionBar().setTitle(
				getResources().getString(R.string.editing_matrix)
						+ currentMatrix + "...");
		checkNewUser();

	}

	private void helpDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.welcome);
		builder.setIcon(R.drawable.ic_action_heart);
		builder.setMessage(R.string.edit_help_message);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});
		builder.show();
	}

	private void checkNewUser() {
		// TODO Auto-generated method stub
		final SharedPreferences prefs = getSharedPreferences("prefs", 0);
		if (prefs.getBoolean("newUser", true)) {

			helpDialog();
			prefs.edit().putBoolean("newUser", false).commit();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		saveChangesDialog("exit");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	protected Matrix loadMatrix(char matrixToLoad) {
		// TODO Auto-generated method stub
		String FILENAME = "matrix" + matrixToLoad;
		File file = getBaseContext().getFileStreamPath(FILENAME);

		if (file.exists()) {
			try {

				return Matrix.read(new BufferedReader(new InputStreamReader(
						openFileInput(FILENAME))));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new Matrix(0, 0);

	}

	private void loadData() {
		// TODO Auto-generated method stub
		final String FILENAME = "matrix" + currentMatrix;
		File file = getBaseContext().getFileStreamPath(FILENAME);

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// display each item in a single line
				Matrix matrix = (Matrix) msg.obj;
				int dataColumns = matrix.getColumnDimension();

				int dataRows = matrix.getRowDimension();
				TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
				for (int a = 1; a < dataColumns; a++) {
					addNewColumn();
				}
				for (int b = 1; b < dataRows; b++) {
					addNewRow();
				}
				for (int i = 0; i < rows; i++) // iterate through rows (all
												// except
												// add
				{
					TableRow tableRow = (TableRow) tableLayout.getChildAt(i);

					for (int x = 0; x < columns; x++) // iterate through
														// edittexts
					{
						DecimalFormat df = new DecimalFormat(
								"###########################################################################.###");

						EditText et = (EditText) tableRow.getChildAt(x);
						et.setText(df.format(matrix.get(i, x))); // TODO:remove
																	// floating
																	// decimal
					}

				}
			}
		};
		if (file.exists()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						Matrix matrix = Matrix
								.read(new BufferedReader(new InputStreamReader(
										openFileInput(FILENAME))));
						Message msg = handler.obtainMessage(0, matrix);
						handler.sendMessage(msg);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		} else {
			findViewById(R.id.addColumn).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							addNewColumn();
						}
					});
			findViewById(R.id.addRow).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addNewRow();
				}
			});
		}
		if (rows < 2) {
			findViewById(R.id.addRow).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addNewRow();
				}
			});
		}
		if (columns < 2) {
			findViewById(R.id.addColumn).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							addNewColumn();
						}
					});
		}
	}

	public void hideExtraControls() {
		Animation fadeInAnimation = AnimationUtils.loadAnimation(
				MatrixMateActivity.this, R.anim.fade_in);
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(
				MatrixMateActivity.this, R.anim.fade_out);
		findViewById(R.id.showExtraControls).setAnimation(fadeInAnimation);
		findViewById(R.id.complexInput).setAnimation(fadeOutAnimation);
		findViewById(R.id.setSize).setAnimation(fadeOutAnimation);
		findViewById(R.id.manipulate).setAnimation(fadeOutAnimation);
		findViewById(R.id.create).setAnimation(fadeOutAnimation);
		findViewById(R.id.clear).setAnimation(fadeOutAnimation);
		fadeOutAnimation.startNow();
		fadeInAnimation.startNow();

		findViewById(R.id.showExtraControls).setVisibility(View.VISIBLE);
		findViewById(R.id.complexInput).setVisibility(View.GONE);
		findViewById(R.id.setSize).setVisibility(View.GONE);
		findViewById(R.id.manipulate).setVisibility(View.GONE);
		findViewById(R.id.create).setVisibility(View.GONE);
		findViewById(R.id.clear).setVisibility(View.GONE);
	}

	private void setOnClickListeners() {
		// TODO Auto-generated method stub
		((CheckBox) findViewById(R.id.complexInput))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

						changeToComplexInput();
					}
				});
		((CheckBox) findViewById(R.id.showExtraControls))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							showExtraControls();
						} else {
							hideExtraControls();
						}
					}
				});
		findViewById(R.id.setSize).setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Builder builder = new Builder(v.getContext());
				builder.setTitle(R.string.set_size);

				View view = getLayoutInflater().inflate(R.layout.sizedialog,
						null);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					android.widget.NumberPicker rowPicker = ((android.widget.NumberPicker) view
							.findViewById(R.id.rowsPicker));
					rowPicker.setMaxValue(20);
					rowPicker.setMinValue(1);
					rowPicker.setValue(rows);

					android.widget.NumberPicker columnsPicker = ((android.widget.NumberPicker) view
							.findViewById(R.id.columnsPicker));
					columnsPicker.setMaxValue(20);
					columnsPicker.setMinValue(1);
					columnsPicker.setValue(columns);
				} else {
					NumberPicker rowPicker = ((NumberPicker) view
							.findViewById(R.id.rowsPicker));
					rowPicker.mCurrent = rows;

					NumberPicker columnPicker = ((NumberPicker) view
							.findViewById(R.id.columnsPicker));
					columnPicker.mCurrent = columns;
				}

				final View finalView = view;
				final AlertDialog alertDialog = builder.create();
				alertDialog.setView(finalView);
				view.findViewById(R.id.done).setOnClickListener(
						new OnClickListener() {

							@SuppressLint("NewApi")
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								int newRows;

								int newColumns;

								if (android.os.Build.VERSION.SDK_INT > 10) {
									newRows = ((android.widget.NumberPicker) finalView
											.findViewById(R.id.rowsPicker))
											.getValue();

									newColumns = ((android.widget.NumberPicker) finalView
											.findViewById(R.id.columnsPicker))
											.getValue();
								} else {
									newRows = ((NumberPicker) finalView
											.findViewById(R.id.rowsPicker)).mCurrent;
									newColumns = ((NumberPicker) finalView
											.findViewById(R.id.columnsPicker)).mCurrent;
								}

								do {
									if (rows < newRows) {
										addNewRow();
									} else if (rows > newRows) {
										deleteRow();
									}
								} while (newRows != rows);
								do {
									if (columns < newColumns) {
										addNewColumn();
									} else if (columns > newColumns) {
										deleteColumn();

									}
								} while (newColumns != columns);
								alertDialog.dismiss();
							}
						});
				alertDialog.show();

			}
		});
		findViewById(R.id.manipulate).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
				for (int i = 0; i < rows; i++) // iterate through rows (all
												// except add
				{
					TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
					for (int x = 0; x < columns; x++) // iterate through
														// edittexts
					{

						EditText et = (EditText) tableRow.getChildAt(x);
						try {
							Evaluator eval = new Evaluator();
							String string = ((EditText) et).getText()
									.toString();
							if (!string.trim().matches("")) {

								((EditText) et).setText(eval.evaluate(string));
							}
						} catch (EvaluationException e) {
							Toast.makeText(v.getContext(),
									e.getLocalizedMessage().toString(),
									Toast.LENGTH_SHORT).show();
						}
					}
				}

				saveMatrix();
				Intent intent = new Intent(getBaseContext(),
						ManipulateActivity.class);
				intent.putExtra("currentMatrix", currentMatrix);
				startActivity(intent);

				MatrixMateActivity.this.finish();
			}
		});
		findViewById(R.id.create).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveMatrix();
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MatrixMateActivity.this);
				alertDialog.setTitle(R.string.choose_matrix);
				final List<String> items = new ArrayList<String>();

				for (int i = 0; i < matrices.length; i++) {
					Matrix currentMatrix = loadMatrix(matrices[i]);
					int rows = currentMatrix.getRowDimension();
					int columns = currentMatrix.getColumnDimension();
					items.add(String.valueOf(matrices[i]) + "(" + rows + "x"
							+ columns + ")");

				}
				final String[] display = new String[items.size()];
				System.arraycopy(items.toArray(), 0, display, 0, items.size());
				alertDialog.setItems(display,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getBaseContext(),
										MatrixMateActivity.class);
								intent.putExtra("currentMatrix",
										display[which].charAt(0));
								startActivity(intent);
								MatrixMateActivity.this.finish();
							}
						});
				alertDialog.create().show();

			}
		});
		findViewById(R.id.clear).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clear();
			}
		});
		findViewById(R.id.e1T1).setOnFocusChangeListener(editTextOnFocus(1, 1));
	}

	private OnFocusChangeListener editTextOnFocus(final int row,
			final int column) {
		return new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (complexInput) {
					if (hasFocus) {
						if (complexStrings.get((row - 1) * columns + column) != null
								&& !complexStrings.get(
										(row - 1) * columns + column).matches(
										"")) {
							((EditText) v).setText(complexStrings.get((row - 1)
									* columns + column));
						}

					} else {
						try {
							Evaluator eval = new Evaluator();
							String string = ((EditText) v).getText().toString();
							if (!string.trim().matches("")) {
								complexStrings.put(
										((row - 1) * columns + column), string);
								((EditText) v).setText(eval.evaluate(string));
							}
						} catch (EvaluationException e) {
							Toast.makeText(v.getContext(),
									e.getLocalizedMessage().toString(),
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		};
	}

	public void showExtraControls() {
		// TODO Auto-generated method stub
		Animation fadeInAnimation = AnimationUtils.loadAnimation(
				MatrixMateActivity.this, R.anim.fade_in);
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(
				MatrixMateActivity.this, R.anim.fade_out);

		findViewById(R.id.setSize).setVisibility(View.VISIBLE);
		findViewById(R.id.manipulate).setVisibility(View.VISIBLE);
		findViewById(R.id.create).setVisibility(View.VISIBLE);
		findViewById(R.id.clear).setVisibility(View.VISIBLE);

		findViewById(R.id.showExtraControls).setVisibility(View.GONE);
		findViewById(R.id.complexInput).setVisibility(View.VISIBLE);
		findViewById(R.id.setSize).setAnimation(fadeInAnimation);
		findViewById(R.id.manipulate).setAnimation(fadeInAnimation);
		findViewById(R.id.create).setAnimation(fadeInAnimation);
		findViewById(R.id.clear).setAnimation(fadeInAnimation);
		findViewById(R.id.showExtraControls).setAnimation(fadeOutAnimation);
		findViewById(R.id.complexInput).setAnimation(fadeInAnimation);
	}

	protected void clear() {
		// TODO Auto-generated method stub
		for (int i = 1; i < rows;) // iterate through rows (all except add and
									// 1st
		{
			deleteRow();

		}
		for (int i = 1; i < columns;) // iterate through columns (all except add
										// and 1st
		{
			deleteColumn();

		}
		((EditText) findViewById(R.id.e1T1)).setText("");
	}

	public void saveMatrix() {

		final Matrix matrix = matrixFromLayout();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String FILENAME = "matrix" + currentMatrix;
				FileOutputStream outStream = null;
				try {
					outStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				PrintWriter out = new PrintWriter(outStream);

				matrix.print(out, 10, 2);
				out.flush();
			}
		});
		thread.start();

	}

	private Matrix matrixFromLayout() {
		double[][] matrixArray = new double[rows][columns];
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		for (int i = 0; i < rows; i++) // iterate through rows (all except add
		{
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			for (int x = 0; x < columns; x++) // iterate through edittexts
			{

				EditText et = (EditText) tableRow.getChildAt(x);
				if (et.getText().toString().matches("")) {
					matrixArray[i][x] = 0;
				} else {
					try {
						if (et.getText().toString().matches("Infinity")) {
							matrixArray[i][x] = 9999999;
						} else {
							matrixArray[i][x] = Double.valueOf(et.getText()
									.toString());
						}
					} catch (NumberFormatException e) {
						Toast.makeText(this, R.string.complex_parse_warning,
								Toast.LENGTH_LONG).show();
						matrixArray[i][x] = 0;
					}
				}
			}
		}
		return new Matrix(matrixArray);
	}

	private void editDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(R.string.edit_matrix);

		final List<String> items = new ArrayList<String>();

		for (int i = 0; i < matrices.length; i++) {
			Matrix currentMatrix = loadMatrix(matrices[i]);
			int rows = currentMatrix.getRowDimension();
			int columns = currentMatrix.getColumnDimension();
			if (rows == 0) {
				items.add(String.valueOf(matrices[i])
						+ getResources().getString(R.string.empty_par));
			} else {
				items.add(String.valueOf(matrices[i]) + "(" + rows + "x"
						+ columns + ")");
			}

		}
		final String[] display = new String[items.size()];
		System.arraycopy(items.toArray(), 0, display, 0, items.size());
		alertDialog.setItems(display, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(),
						MatrixMateActivity.class);
				intent.putExtra("currentMatrix", display[which].charAt(0));
				startActivity(intent);
				MatrixMateActivity.this.finish();
			}
		});
		alertDialog.create().show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.actionbaredit, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case R.id.menu_edit:
			saveChangesDialog("edit");
			return true;
		case R.id.menu_manipulate:
			saveChangesDialog("manipulate");
			return true;
		case R.id.feedback:
			new ReportDialog(this);
			return true;
		case R.id.help:
			helpDialog();
			return true;
	
			/*
		case R.id.donate:
			donateDialog();
			return true;
			*/
	
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void changeToComplexInput() {
		// TODO Auto-generated method stub
		
			if (complexInput) {
				complexInput = false;
				disableComplexInput();
			} else {
				Toast.makeText(this, R.string.complex_enable, Toast.LENGTH_LONG)
						.show();
				complexInput = true;
				enableComplexInput();
			}
		
	}

	private void enableComplexInput() {
		// TODO Auto-generated method stub
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		for (int i = 0; i < rows; i++) // iterate through rows (all except add
		{
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			for (int x = 0; x < columns; x++) // iterate through edittexts
			{

				EditText et = (EditText) tableRow.getChildAt(x);
				et.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			}
		}

	}

	private void disableComplexInput() {
		// TODO Auto-generated method stub
		complexStrings = new LinkedHashMap<Integer, String>();
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		for (int i = 0; i < rows; i++) // iterate through rows (all except add
		{
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			for (int x = 0; x < columns; x++) // iterate through edittexts
			{

				EditText et = (EditText) tableRow.getChildAt(x);
				et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);

			}
		}

	}

	

	
/*
	protected void donateDialog() {
		// TODO Auto-generated method stub
		PayPal pp = PayPal.getInstance();

		if (pp == null) { // Test to see if the library is already initialized

			// This main initialization call takes your Context, AppID, and
			// target server
			pp = PayPal.initWithAppID(this, "APP-80W284485P519543T",
					PayPal.ENV_SANDBOX);

			// Required settings:

			// Some Optional settings:

			// Sets who pays any transaction fees. Possible values are:
			// FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER,
			// and FEEPAYER_SECONDARYONLY

			// true = transaction requires shipping
			pp.setShippingEnabled(false);
		}
		CheckoutButton launchPayPalButton = pp.getCheckoutButton(this,
				PayPal.BUTTON_194x37, CheckoutButton.TEXT_DONATE); // button
																	// size
																	// 278x43
																	// and text
																	// on it is
																	// 'PAY'
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		launchPayPalButton.setLayoutParams(params);
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.thank_you);
		final View view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.donatedialog, null);
		final AlertDialog alertDialog = builder.create();
		alertDialog.setView(view);
		launchPayPalButton.setId(1);
		launchPayPalButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String amount = ((EditText) view
						.findViewById(R.id.donateAmount)).getText().toString();
				if (!amount.matches("")) {
					PayPalPayment payment = new PayPalPayment();
					// Sets the currency type for this payment.
					payment.setCurrencyType("USD");
					// Sets the recipient for the payment. This can also be a
					// phone number.
					payment.setRecipient("senry_1341944864_biz@hotmail.com");
					// Sets the amount of the payment, not including tax and
					// shipping amounts.
					payment.setSubtotal(new BigDecimal(amount));
					// Sets the payment type. This can be PAYMENT_TYPE_GOODS,
					// PAYMENT_TYPE_SERVICE, PAYMENT_TYPE_PERSONAL, or
					// PAYMENT_TYPE_NONE.
					payment.setPaymentType(PayPal.PAYMENT_TYPE_PERSONAL);

					// PayPalInvoiceData can contain tax and shipping amounts.
					// It also contains an ArrayList of PayPalInvoiceItem which
					// can
					// be filled out. These are not required for any
					// transaction.

					payment.setPaymentSubtype(PayPal.PAYMENT_SUBTYPE_DONATIONS);

					// Sets the merchant name. This is the name of your
					// Application or Company.
					payment.setMerchantName("Matrix Mate");
					// Sets the memo. This memo will be part of the notification
					// sent by PayPal to the necessary parties.
					payment.setMemo("Thank you so much for your donation :)");
					donateDialog = alertDialog;
					Intent paypalIntent = PayPal.getInstance().checkout(
							payment, v.getContext());
					startActivityForResult(paypalIntent, 1);
				} else {
					Toast.makeText(v.getContext(), R.string.donate_empty,
							Toast.LENGTH_SHORT).show();
					((CheckoutButton) v).updateButton();
				}
			}
		});

		((LinearLayout) view.findViewById(R.id.donateLayout))
				.addView(launchPayPalButton);
		alertDialog.show();

	}
*/
	public void saveChangesDialog(final String purpose) {
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		saveDialog.setTitle(R.string.app_name);
		saveDialog.setMessage(R.string.save_changes);
		saveDialog.setPositiveButton(R.string.yes,
				new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						saveMatrix();
						if (purpose.matches("edit")) {
							editDialog();

						} else if (purpose.matches("exit")) {
							finish();
						} else {

							MatrixMateActivity.this.finish();

						}
					}
				});
		saveDialog.setNegativeButton(R.string.no, new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (purpose.matches("edit")) {
					editDialog();
				} else if (purpose.matches("exit")) {
					finish();
				} else {

					MatrixMateActivity.this.finish();
				}
			}
		});
		saveDialog.show();
	}

	public void addNewRow() {
		rows++;
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		try {
			tableLayout.removeViewAt(rows);
		} catch (Exception e) {
		}
		TableRow tableRow = (TableRow) tableLayout.getChildAt(rows - 1);
		tableRow.removeAllViews();
		for (int i = 1; i < columns + 1; i++) {
			EditText et = new EditText(this); //
			TableRow.LayoutParams params = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (complexInput) {
				et.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			} else {
				et.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_SIGNED
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}

			et.setMinEms(2);
			et.setMaxEms(5);
			et.setId(rows * 10 + i); // new row
			tableRow.addView(et, params);
			et.setOnFocusChangeListener(editTextOnFocus(rows, i));
			if (i == 1) {
				et.requestFocus();
			}
		}
		addDeleteButtonsRows();

		setFocusOrder();
	}

	public void deleteRow() {
		rows--;
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		tableLayout.removeViewAt(rows);
		tableLayout.removeViewAt(rows);
		tableLayout.removeViewAt(rows);

		addDeleteButtonsRows();
		setFocusOrder();
	}

	private void addDeleteButtonsRows() {
		// TODO Auto-generated method stub

		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		TableRow tableRow = (TableRow) tableLayout.getChildAt(rows - 1);
		LayoutParams params = new TableRow.LayoutParams(
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						50, getResources().getDisplayMetrics()),
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						50, getResources().getDisplayMetrics()));

		tableRow = new TableRow(this);
		tableRow.setLayoutParams(new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		tableLayout.addView(tableRow);
		ImageButton imgBtn = new ImageButton(this);

		imgBtn.setBackgroundDrawable(null);
		imgBtn.setImageResource(R.drawable.ic_action_add);
		imgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNewRow();
			}
		});
		tableRow.addView(imgBtn, params);

		if (rows > 1) {
			tableRow = new TableRow(this);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			tableLayout.addView(tableRow);
			imgBtn = new ImageButton(this);

			TableRow.LayoutParams tableParams = new TableRow.LayoutParams(1);
			imgBtn.setLayoutParams(tableParams);
			imgBtn.setBackgroundDrawable(null);
			imgBtn.setImageResource(R.drawable.ic_action_delete);
			imgBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteRow();
				}
			});
			tableRow.addView(imgBtn, params);
		}
	}

	public void addNewColumn() {
		columns++;
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

		TableRow tableRow = (TableRow) tableLayout.getChildAt(0);
		tableRow.removeViewAt(columns - 1);
		try {
			tableRow.removeViewAt(columns - 1);
		} catch (Exception e) {
		}
		if (complexInput) {
			LinkedHashMap<Integer, String> tempComplexStrings = new LinkedHashMap<Integer, String>();
			for (int a = 0; a < rows; a++) // iterate through rows (all except
											// // add
			{
				TableRow tRow = (TableRow) tableLayout.getChildAt(a);

				for (int x = 1; x < columns; x++) // iterate through edittexts
				{
					if (complexStrings.get(a * (columns - 1) + x) != null) {

						tempComplexStrings.put(a * (columns - 1) + x + a,
								complexStrings.get(a * (columns - 1) + x));
					}
					EditText etComplex = (EditText) tRow.getChildAt(x - 1);
					etComplex
							.setOnFocusChangeListener(editTextOnFocus(a + 1, x));
				}

			}
			complexStrings = tempComplexStrings;
		}
		for (int i = 1; i < rows + 1; i++) {
			TableRow oldTableRow = (TableRow) tableLayout.getChildAt(i - 1);
			EditText et = new EditText(this); //
			TableRow.LayoutParams params = new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			et.setMinEms(2);
			et.setMaxEms(5);
			if (complexInput) {
				et.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			} else {
				et.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_SIGNED
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}
			et.setId(i * 10 + columns); // new row
			oldTableRow.addView(et, params);
			// reorder complex

			et.setOnFocusChangeListener(editTextOnFocus(i, columns));
			if (i == 1) {
				et.requestFocus();
			}

		}
		addDeleteButtonsColumn();
		setFocusOrder();
	}

	private void addDeleteButtonsColumn() {
		// TODO Auto-generated method stub
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		TableRow tableRow = (TableRow) tableLayout.getChildAt(0);
		ImageButton imgBtn = new ImageButton(this);

		imgBtn.setBackgroundDrawable(null);
		imgBtn.setImageResource(R.drawable.ic_action_add);
		imgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addNewColumn();
			}
		});

		tableRow.addView(imgBtn);
		if (columns > 1) {
			imgBtn = new ImageButton(this);

			imgBtn.setBackgroundDrawable(null);
			imgBtn.setImageResource(R.drawable.ic_action_delete);
			imgBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					deleteColumn();
				}
			});
			tableRow.addView(imgBtn);
		}
	}

	private void deleteColumn() {
		columns--;
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		if (complexInput) {
			LinkedHashMap<Integer, String> tempComplexStrings = new LinkedHashMap<Integer, String>();
			for (int a = 0; a < rows; a++) // iterate through rows (all except
											// // add
			{
				TableRow tRow = (TableRow) tableLayout.getChildAt(a);

				for (int x = 1; x < columns + 2; x++) // iterate through
														// edittexts
				{
					if (complexStrings.get(a * (columns + 1) + x) != null) {

						tempComplexStrings.put(a * (columns + 1) + x - a,
								complexStrings.get(a * (columns + 1) + x));
					}
					if (x < columns + 1) {
						EditText etComplex = (EditText) tRow.getChildAt(x - 1);
						etComplex.setOnFocusChangeListener(editTextOnFocus(
								a + 1, x));
					}
				}

			}
			complexStrings = tempComplexStrings;
		}
		TableRow tableRow = (TableRow) tableLayout.getChildAt(0);
		tableRow.removeViewAt(columns + 1);
		tableRow.removeViewAt(columns + 1);
		for (int i = 0; i < rows; i++) {
			tableRow = (TableRow) tableLayout.getChildAt(i);
			tableRow.removeViewAt(columns);
		}

		addDeleteButtonsColumn();
		setFocusOrder();
	}

	private void setFocusOrder() {
		// TODO Auto-generated method stub
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		for (int i = 0; i < rows; i++) // iterate through rows (all except add
		{
			TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
			for (int x = 0; x < columns; x++) // iterate through edittexts
			{
				if (x < columns - 1) {
					tableRow.getChildAt(x).setNextFocusDownId(
							tableRow.getChildAt(x + 1).getId());
				} else if (i < rows - 1) {
					TableRow tableRowNext = (TableRow) tableLayout
							.getChildAt(i + 1);
					tableRow.getChildAt(x).setNextFocusDownId(
							tableRowNext.getChildAt(0).getId());
				}
			}
		}
	}
/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case Activity.RESULT_OK:
			// The payment succeeded
			// Tell the user their payment succeeded
			Toast.makeText(this, R.string.thank_you, Toast.LENGTH_LONG).show();
			donateDialog.dismiss();
			break;
		case Activity.RESULT_CANCELED:
			// The payment was canceled
			// Tell the user their payment was canceled
			donateDialog.dismiss();
			break;
		case PayPalActivity.RESULT_FAILURE:
			Toast.makeText(this, R.string.consideration, Toast.LENGTH_LONG)
					.show();
			donateDialog.dismiss();
			// The payment failed -- we get the error from the EXTRA_ERROR_ID
			// and EXTRA_ERROR_MESSAGE

			// Tell the user their payment was failed.
		}
	}
*/
}
