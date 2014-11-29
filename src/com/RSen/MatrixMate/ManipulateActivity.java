package com.RSen.MatrixMate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ManipulateActivity extends SherlockActivity {
	Matrix matrix1;
	char matrix1Name;
	Matrix matrix2;
	char matrix2Name;
	Matrix resultMatrix;
	String operation;
	char[] matrices;

	/*
	 * TextView bottomMostTextView; //to create dynamic layouts ImageButton
	 * bottomMostImageButton;
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

		setContentView(R.layout.manipulate);
		
	
		matrices = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
		

		setClickListeners();

		checkNewUser();
		new SimpleEula(this).show();

		
		
		
	}

	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	private void checkNewUser() {
		// TODO Auto-generated method stub
		final SharedPreferences prefs = getSharedPreferences("prefs", 0);
		if (prefs.getBoolean("newUser", true)) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.welcome);
			builder.setIcon(R.drawable.ic_action_heart);
			builder.setMessage(R.string.to_start_please_create_matrix);

			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getBaseContext(),
									MatrixMateActivity.class);
							intent.putExtra("currentMatrix", 'A');
							startActivity(intent);
							prefs.edit().putBoolean("newUser", false).commit();

						}
					});
			builder.show();

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		fadeViewsIn();
		if (getIntent().getCharExtra("currentMatrix", 'z') != 'z') {
			matrix1 = loadMatrix(getIntent().getCharExtra("currentMatrix", 'z'));
			((TextView) findViewById(R.id.textView1)).setText("A("
					+ matrix1.getRowDimension() + "x"
					+ matrix1.getColumnDimension() + ")");
			changeMatrixPicture(getIntent().getCharExtra("currentMatrix", 'z'),
					1);
			showOperationLayout();
		}
		
		super.onResume();

	}

	private void refresh() {
		if(matrix1!= null)
		{
		try
		{
			matrix1.get(0, 0);
		}
		catch(Exception e)
		{
			matrix1 = loadMatrix(matrix1Name);
			((TextView) findViewById(R.id.textView1)).setText("A("
					+ matrix1.getRowDimension() + "x"
					+ matrix1.getColumnDimension() + ")");
		}
		}
		if(matrix2!=null)
		{
		try
		{
			matrix2.get(0,0);
		}
		catch (Exception e)
		{
			((TextView) findViewById(R.id.textView3)).setText("A("
					+ matrix1.getRowDimension() + "x"
					+ matrix1.getColumnDimension() + ")");
		}
		}
		if (resultMatrix!=null)
		{
		try
		{
			resultMatrix.get(0,0);
		}
		catch (Exception e)
		{
			eraseResults();
			calculateResult();
			showResult();
		}
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub

		refresh();

		super.onRestart();
	}

	private void fadeViewsIn() {
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.fade_in);

		findViewById(R.id.manipulateLayout).startAnimation(fadeInAnimation);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.actionbarmanipulate, menu);
		
		return true;
	}
	/*
	protected void donateDialog() {
		// TODO Auto-generated method stub
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// display each item in a single line
				CheckoutButton launchPayPalButton = (CheckoutButton) msg.obj;
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				launchPayPalButton.setLayoutParams(params);
				AlertDialog.Builder builder = new Builder(
						ManipulateActivity.this);
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
								.findViewById(R.id.donateAmount)).getText()
								.toString();
						if (!amount.matches("")) {
							PayPalPayment payment = new PayPalPayment();
							// Sets the currency type for this payment.
							payment.setCurrencyType("USD");
							// Sets the recipient for the payment. This can also
							// be a phone number.
							payment.setRecipient("RSenApps@gmail.com");
							// Sets the amount of the payment, not including tax
							// and shipping amounts.
							payment.setSubtotal(new BigDecimal(amount));
							// Sets the payment type. This can be
							// PAYMENT_TYPE_GOODS, PAYMENT_TYPE_SERVICE,
							// PAYMENT_TYPE_PERSONAL, or PAYMENT_TYPE_NONE.
							payment.setPaymentType(PayPal.PAYMENT_TYPE_PERSONAL);

							// PayPalInvoiceData can contain tax and shipping
							// amounts. It also contains an ArrayList of
							// PayPalInvoiceItem which can
							// be filled out. These are not required for any
							// transaction.

							payment.setPaymentSubtype(PayPal.PAYMENT_SUBTYPE_DONATIONS);

							// Sets the merchant name. This is the name of your
							// Application or Company.
							payment.setMerchantName("Matrix Mate");
							// Sets the memo. This memo will be part of the
							// notification sent by PayPal to the necessary
							// parties.
							payment.setMemo("Thank you so much for your donation :)");
							donateDialog = alertDialog;
							Intent paypalIntent = PayPal.getInstance()
									.checkout(payment, v.getContext());
							startActivityForResult(paypalIntent, 1);
						} else {
							Toast.makeText(v.getContext(),
									R.string.donate_empty, Toast.LENGTH_SHORT)
									.show();
							((CheckoutButton) v).updateButton();
						}
					}
				});

				((LinearLayout) view.findViewById(R.id.donateLayout))
						.addView(launchPayPalButton);
				alertDialog.show();
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				PayPal pp = PayPal.getInstance();

				if (pp == null) { // Test to see if the library is already
									// initialized

					// This main initialization call takes your Context, AppID,
					// and target server
					pp = PayPal.initWithAppID(ManipulateActivity.this,
							"APP-2YC67859UH068035D", PayPal.ENV_LIVE);
					pp.setShippingEnabled(false);
				}
				CheckoutButton launchPayPalButton = pp.getCheckoutButton(
						ManipulateActivity.this, PayPal.BUTTON_194x37,
						CheckoutButton.TEXT_DONATE); // button size 278x43 and
														// text on it is 'PAY'

				Message msg = handler.obtainMessage(0, launchPayPalButton);
				handler.sendMessage(msg);
			}
		}).start();

	}
	*/
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case R.id.menu_edit:
			editSelectionDialog();
			return true;
		case R.id.feedback:
			new ReportDialog(this);
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

	private void editSelectionDialog() {
		// TODO Auto-generated method stub
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
			}
		});
		alertDialog.create().show();
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		findViewById(R.id.matrix1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				eraseResults();
				chooseMatrix(1);

			}
		});
		findViewById(R.id.matrix2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * bottomMostImageButton = (ImageButton) v; bottomMostTextView =
				 * (TextView) findViewById(R.id.textView3);
				 */
				eraseResults();
				chooseMatrix(2);
			}
		});
		findViewById(R.id.operation).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * bottomMostImageButton = (ImageButton) v; bottomMostTextView =
				 * (TextView) findViewById(R.id.textView3);
				 */
				eraseResults();
				chooseOperation();
			}
		});
		findViewById(R.id.save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				saveResultMatrix();
				refresh();
			}
		});
		/*
		 * findViewById(R.id.addOperation).setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub if (paid) { eraseResults(); showNewOperationLayout(); } else {
		 * showUpgradeDialog(); } } });
		 */
	}

	protected void showOperationLayout() {
		// TODO Auto-generated method stub
		findViewById(R.id.textView2).setVisibility(View.VISIBLE);
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.fade_in);
		findViewById(R.id.textView2).setAnimation(fadeInAnimation);

		findViewById(R.id.operation).setVisibility(View.VISIBLE);
		findViewById(R.id.operation).setAnimation(fadeInAnimation);
	}

	/*
	 * protected void showNewOperationLayout() { // TODO Auto-generated method
	 * stub ImageButton newImageButton = new ImageButton(this);
	 * RelativeLayout.LayoutParams params = new
	 * RelativeLayout.LayoutParams((int)
	 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75,
	 * getResources().getDisplayMetrics()), (int)
	 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75,
	 * getResources().getDisplayMetrics()));
	 * params.addRule(RelativeLayout.BELOW, bottomMostImageButton.getId());
	 * params.addRule(RelativeLayout.ALIGN_LEFT, R.id.matrix1);
	 * params.setMargins(0, (int)
	 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
	 * getResources().getDisplayMetrics()), 0, 0);
	 * newImageButton.setImageResource(R.drawable.ic_action_operations);
	 * ((RelativeLayout)
	 * findViewById(R.id.manipulateLayout)).addView(newImageButton, params);
	 * 
	 * TextView newTextView = new TextView(this);
	 * 
	 * params = new
	 * RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
	 * RelativeLayout.LayoutParams.WRAP_CONTENT);
	 * params.addRule(RelativeLayout.BELOW, bottomMostImageButton.getId());
	 * params.addRule(RelativeLayout.ALIGN_LEFT, R.id.textView1);
	 * params.setMargins(0, (int)
	 * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45,
	 * getResources().getDisplayMetrics()), 0, 0);
	 * newTextView.setText(R.string.choose_operation);
	 * newTextView.setTextAppearance(this, android.R.attr.textAppearanceLarge);
	 * ((RelativeLayout)
	 * findViewById(R.id.manipulateLayout)).addView(newTextView, params);
	 * 
	 * }
	 */
	
	public void saveResultMatrix() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(R.string.save_as);
		final List<String> items = new ArrayList<String>();

		for (int i = 0; i < matrices.length; i++) {
			Matrix currentMatrix = loadMatrix(matrices[i]);
			int rows = currentMatrix.getRowDimension();
			int columns = currentMatrix.getColumnDimension();
			items.add(String.valueOf(matrices[i]) + "(" + rows + "x" + columns
					+ ")");

		}
		final String[] display = new String[items.size()];
		System.arraycopy(items.toArray(), 0, display, 0, items.size());
		alertDialog.setItems(display, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, final int which) {
				// TODO Auto-generated method stub
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						String FILENAME = "matrix" + display[which].charAt(0);
						FileOutputStream outStream = null;
						try {
							outStream = openFileOutput(FILENAME,
									Context.MODE_PRIVATE);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						PrintWriter out = new PrintWriter(outStream);

						resultMatrix.print(out, 10, 2);
						out.flush();
					}
				});
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();

			}
		});
		alertDialog.create().show();

	}

	protected void chooseOperation() {
		// divide, multiply, Cholesky, Matrix Condtion(2 norm), det, eigan
		// value, identity, inverse, lu, subtract, norm 1,2,F,Infinity,plus, qr,
		// rank,solve, svd, trace, transpose, unary minus
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(R.string.choose_operation);

		final String[] operations;
		final List<String> items = new ArrayList<String>();
		if (matrix1.getRowDimension() == matrix1.getColumnDimension()) {

			operations = getResources().getStringArray(
					R.array.operations_square);
			for (int i = 0; i < operations.length; i++) {

				if (i == 6) {
					if (matrix1.getRowDimension() == matrix1
							.getColumnDimension() && matrix1.det() != 0) {
						items.add(operations[i]);
					}
				} else if (i == 4) {
					if (matrix1.getRowDimension() == matrix1
							.getColumnDimension() && matrix1.det() != 0) {
						items.add(operations[i]);
					}
				} else {
					items.add(operations[i]);
				}

			}

		} else {
			operations = getResources().getStringArray(R.array.operations_rect);
			for (int i = 0; i < operations.length; i++) {
				if (i!=5)
				{
					items.add(operations[i]);
				}
				else if (matrix1.getRowDimension() >= matrix1.getColumnDimension()) {
					items.add(operations[i]);
				}
			}
		}

		final String[] display = new String[items.size()];
		System.arraycopy(items.toArray(), 0, display, 0, items.size());
		alertDialog.setItems(display, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				operation = (String) display[which];
				String[] matrix2Operations = getResources().getStringArray(
						R.array.operations_two_matrix);
				((TextView) findViewById(R.id.textView2)).setText(operation);
				Boolean twoMatrices = false;
				for (int i = 0; i < matrix2Operations.length; i++) {
					if (operation.matches(matrix2Operations[i])) {
						twoMatrices = true;

					}
				}
				if (twoMatrices) {
					((TextView) findViewById(R.id.textView3))
							.setText(R.string.choose_matrix);
					changeMatrixPicture('z', 2);
					showMatrix2Layout();
				} else {
					if (findViewById(R.id.matrix2).getVisibility() == View.VISIBLE) {
						hideMatrix2Layout();
					}
					calculateResult();
					showResult();
				}
			}
		});
		alertDialog.create().show();
	}

	protected void showMatrix2Layout() {
		// TODO Auto-generated method stub
		findViewById(R.id.textView3).setVisibility(View.VISIBLE);
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.fade_in);
		findViewById(R.id.textView3).setAnimation(fadeInAnimation);

		findViewById(R.id.matrix2).setVisibility(View.VISIBLE);
		findViewById(R.id.matrix2).setAnimation(fadeInAnimation);

	}

	private void hideMatrix2Layout() {

		Animation fadeOutAnimation = AnimationUtils.loadAnimation(this,
				R.anim.fade_out);
		findViewById(R.id.textView3).setAnimation(fadeOutAnimation);
		findViewById(R.id.textView3).setVisibility(View.GONE);

		findViewById(R.id.matrix2).setAnimation(fadeOutAnimation);
		findViewById(R.id.matrix2).setVisibility(View.GONE);
	}

	protected void chooseMatrix(int matrix) {
		// TODO Auto-generated method stub
		if (matrix == 1) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(R.string.choose_matrix);
			final List<String> items = new ArrayList<String>();
			for (int i = 0; i < matrices.length; i++) {
				Matrix currentMatrix = loadMatrix(matrices[i]);
				int rows = currentMatrix.getRowDimension();
				int columns = currentMatrix.getColumnDimension();
				if (rows > 0) {

					items.add(String.valueOf(matrices[i]) + "(" + rows + "x"
							+ columns + ")");
				}
			}

			if (items.size() == 0) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle(R.string.no_valid_matrices);
				builder.setMessage(R.string.create_matrix_first);
				builder.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								editSelectionDialog();
							}
						});
				builder.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				builder.show();
			} else {
				items.add(getResources().getString(R.string.edit_a_matrix));
				final String[] display = new String[items.size()];

				System.arraycopy(items.toArray(), 0, display, 0, items.size());

				alertDialog.setItems(display,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (display[which] == getResources().getString(
										R.string.edit_a_matrix)) {

									editSelectionDialog();
								} else {
									matrix1Name = display[which].charAt(0);
									matrix1 = loadMatrix(matrix1Name);
									((TextView) findViewById(R.id.textView1))
											.setText(display[which]);
									((TextView) findViewById(R.id.textView2))
											.setText(R.string.choose_operation);
									((TextView) findViewById(R.id.textView3))
											.setText(R.string.choose_matrix);
									changeMatrixPicture(matrix1Name, 1);
									changeMatrixPicture('z', 2);
									hideMatrix2Layout();
									showOperationLayout();
								}

							}
						});
				alertDialog.create().show();
			}
		} else { // choose matrix 2
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(R.string.choose_matrix);
			final List<String> items = new ArrayList<String>();
			final String[] operations = getResources().getStringArray(
					R.array.operations_two_matrix);
			for (int i = 0; i < matrices.length; i++) {
				Matrix currentMatrix = loadMatrix(matrices[i]);
				int rows = currentMatrix.getRowDimension();
				int columns = currentMatrix.getColumnDimension();

				/*
				 * if (operation.matches("+") || operation.matches("-")) { if
				 * (rows == matrix1.getRowDimension() && columns
				 * ==matrix1.getColumnDimension()) { items.add(matrices[i]); } }
				 */
				if (rows > 0) {
					if (operation.matches(operations[2])) {
						if (rows == matrix1.getColumnDimension()) {
							items.add(String.valueOf(matrices[i]) + "(" + rows
									+ "x" + columns + ")");
						}
					} else if (operation.matches("\\+")
							|| operation.matches("\\-")) {
						if (rows == matrix1.getRowDimension()
								&& columns == matrix1.getColumnDimension()) {
							items.add(String.valueOf(matrices[i]) + "(" + rows
									+ "x" + columns + ")");
						}
					} else if (operation.matches(operations[3])) {
						if (rows == columns && currentMatrix.det() != 0
								&& rows == matrix1.getRowDimension()) {
							items.add(String.valueOf(matrices[i]) + "(" + rows
									+ "x" + columns + ")");
						}
					} else {
						items.add(String.valueOf(matrices[i]) + "(" + rows
								+ "x" + columns + ")");
					}
				}
			}

			if (items.size() == 0) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle(R.string.no_valid_matrices);
				builder.setMessage(R.string.create_matrix_first);
				builder.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								editSelectionDialog();
							}
						});
				builder.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				builder.show();
			} else {
				items.add(getResources().getString(R.string.edit_a_matrix));
				final String[] display = new String[items.size()];
				System.arraycopy(items.toArray(), 0, display, 0, items.size());
				alertDialog.setItems(display,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (display[which] == getResources().getString(
										R.string.edit_a_matrix)) {

									editSelectionDialog();
								} else {
									matrix2Name = display[which].charAt(0);
									matrix2 = loadMatrix(matrix2Name);
									changeMatrixPicture(matrix2Name, 2);
									((TextView) findViewById(R.id.textView3))
											.setText(display[which]);
									final Handler handler = new Handler() {
										public void handleMessage(Message msg) {
											showResult();
										};
									};
									new Thread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											calculateResult();
											handler.sendEmptyMessage(0);
										}
									}).start();

								}
							}
						});
				alertDialog.create().show();
			}
		}
	}

	private void eraseResults() {
		resultMatrix = null;
		TableLayout tLayout = (TableLayout) findViewById(R.id.table);
		tLayout.removeAllViews();
		findViewById(R.id.resultButtons).setVisibility(View.GONE);
	}

	protected void showResult() {
		// TODO Auto-generated method stub
		TableLayout tLayout = (TableLayout) findViewById(R.id.table);
		for (int i = 0; i < resultMatrix.getRowDimension(); i++) {
			TableRow tRow = new TableRow(this);
			tLayout.addView(tRow);
		}
		for (int i = 0; i < resultMatrix.getRowDimension(); i++) // iterate
																	// through
																	// rows (all
																	// except
																	// add
		{
			TableRow tableRow = (TableRow) tLayout.getChildAt(i);
			for (int x = 0; x < resultMatrix.getColumnDimension(); x++) // iterate
																		// through
																		// edittexts
			{

				EditText et = new EditText(this);
				et.setText(String.valueOf(resultMatrix.get(i, x)));
				tableRow.addView(et);
			}
		}
		findViewById(R.id.resultButtons).setVisibility(View.VISIBLE);
		
	}

	protected void calculateResult() {
		// TODO Auto-generated method stub
		final String[] operations = getResources().getStringArray(
				R.array.operations_square);
		operations[4] = getResources().getStringArray(
				R.array.operations_two_matrix)[3];
		if (operation.matches("\\+")) {
			resultMatrix = matrix1.plus(matrix2);
		} else if (operation.matches("\\-")) {
			resultMatrix = matrix1.minus(matrix2);
		} else if (operation.matches(operations[2])) {
			resultMatrix = matrix1.times(matrix2);
		} else if (operation.matches(operations[3])) {
			resultMatrix = matrix1.transpose();
		} else if (operation.matches(operations[4])) {
			resultMatrix = matrix1.solve(matrix2);
		} else if (operation.matches(operations[5])) {
			double[][] result = new double[1][1];
			result[0][0] = matrix1.det();
			resultMatrix = new Matrix(result);
		} else if (operation.matches(operations[6])) {
			resultMatrix = matrix1.inverse();
		} else if (operation.matches(operations[7])) {
			resultMatrix = matrix1.eig().getD();
		} else if (operation.matches(operations[8])) {
			resultMatrix = matrix1.eig().getV();
		} else if (operation.matches(operations[9])) {
			resultMatrix = new Matrix(1, 1, matrix1.rank());
		} else if (operation.matches(operations[10])) {
			resultMatrix = new Matrix(1, 1, matrix1.trace());
		} else if (operation.matches(operations[11])) {
			resultMatrix = matrix1.lu().getL();
		} else if (operation.matches(operations[12])) {
			resultMatrix = matrix1.lu().getU();
		} else if (operation.matches(operations[13])) {
			resultMatrix = matrix1.chol().getL();
		} else if (operation.matches(operations[14])) {
			resultMatrix = matrix1.qr().getQ();
		} else if (operation.matches(operations[15])) {
			resultMatrix = matrix1.qr().getR();
		} else if (operation.matches(operations[16])) {
			resultMatrix = matrix1.qr().getH();
		} else if (operation.matches(operations[17])) {
			resultMatrix = matrix1.svd().getS();
		}

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

	private void changeMatrixPicture(char matrixName, int matrixNumber) {

		ImageButton imgBtn;
		if (matrixNumber == 1) {
			imgBtn = (ImageButton) findViewById(R.id.matrix1);
		} else {
			imgBtn = (ImageButton) findViewById(R.id.matrix2);
		}
		int image = 0;
		switch (matrixName) {
		case 'A':
			image = R.drawable.ic_action_aicon;
			break;
		case 'B':
			image = R.drawable.ic_action_bicon;
			break;
		case 'C':
			image = R.drawable.ic_action_cicon;
			break;
		case 'D':
			image = R.drawable.ic_action_dicon;
			break;
		case 'E':
			image = R.drawable.ic_action_eicon;
			break;
		case 'F':
			image = R.drawable.ic_action_ficon;
			break;
		case 'G':
			image = R.drawable.ic_action_gicon;
			break;
		case 'z':
			image = R.drawable.ic_action_emptymatrix;
		}
		imgBtn.setImageResource(image);
	}
}
