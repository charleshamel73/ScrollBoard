package com.example.keyboardv6;


import com.example.keyboardv6.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private GestureDetector gKeyDetector;
	private GestureDetector gTextDetector;
	// Camera variables
	private CameraSurfaceView cameraView;
	private FrameLayout frameNew;
	// different keyboards that can be in the program
	private String[] keys;
	private final int KEYBOARD_COUNT = 3;
	private int currentKeyboard = 0;
	private boolean CAPS_LOCK = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		keys = new String[KEYBOARD_COUNT];
		// lowercase
		keys[0] = "abcdefghijklmnopqrstuvwxyz";
		// uppercase
		keys[1] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		// utility
		keys[2] = "., !?1234567890@";

		// sets up camera to show in Frame liveFeed
		cameraView = new CameraSurfaceView(this);
		frameNew = (FrameLayout) findViewById(R.id.livefeed);
		frameNew.addView(cameraView);

		HorizontalScrollView textHolder = (HorizontalScrollView) findViewById(R.id.textHolder);
		HorizontalScrollView keyHolder = (HorizontalScrollView) findViewById(R.id.keyHolder);
		textHolder.bringToFront();
		keyHolder.bringToFront();

		// gesture detectors
		gKeyDetector = new GestureDetector(this,
				new MyGestureDetector());
		gTextDetector = new GestureDetector(this,
				new MyTextGestureDetector());
		/**
		 * sets up gestures for keyboard Current Gestures: -Up:add letter
		 * -Down:changes keyboard to utility -Left/Right: Scroll
		 * -DoubleTap:Capitalizes normal characters
		 **/
		LinearLayout key = (LinearLayout)  findViewById(R.id.key);
		key.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.d("KEY", "msg: TOUCH REGISTERED");
				arg0.getParent().requestDisallowInterceptTouchEvent(true);
				gKeyDetector.onTouchEvent(arg1);
				return true;
			}
		});

		/**
		 * sets up gestures for text Current Gestures: -Up:clear sentence
		 * -Down:deletes character -Left/Right: Scroll -DoubleTap:adds spaces
		 * iff no space is present
		 **/
		LinearLayout text = (LinearLayout)  findViewById(R.id.text);
		text.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.d("TEXT", "msg: TOUCH REGISTERED");
				arg0.getParent().requestDisallowInterceptTouchEvent(true);
				gTextDetector.onTouchEvent(arg1);
				return true;
			}
		});
		createKeyboard(key);
	}
	
	public void onToggleClicked(View view) {
		ToggleButton toggle = (ToggleButton) view;
		boolean on = toggle.isChecked();
		if (on) {
			CAPS_LOCK = true;
			if (currentKeyboard == 0) {
				LinearLayout key = (LinearLayout) findViewById(R.id.key);
				currentKeyboard = 1;
				createKeyboard(key);
			}
		} else {
			CAPS_LOCK = false;
			if (currentKeyboard == 1) {
				LinearLayout key = (LinearLayout) findViewById(R.id.key);
				currentKeyboard = 0;
				createKeyboard(key);
			}
		}
	}
	
	/**
	 * This method creates a keyboard based off the String in keys in the view
	 * designated
	 * 
	 * @param view
	 *            A Linear Layout that will contain the keys
	 */
	private void createKeyboard(LinearLayout view) {
		// clears current keyboard
		view.removeAllViews();
		String keyString = keys[currentKeyboard];
		for (int i = 0; i < keyString.length(); i++) {
			Button b = new Button(this);
			// 97 is the char a in ascii
			b.setPadding(10, 0, 10, 0);
			String x = String.valueOf(keyString.charAt(i));
			b.setText(x);
			b.setHeight(90);
			b.setWidth(80);
			b.setClickable(false);
			view.addView(b);
		}
	}

	/**
	 * This method copies the button at position x on the keyboard and adds it
	 * to the text field
	 * 
	 * @param x
	 *            An int representation for the x location of the move event
	 */
	private void createButton(float x) {
		LinearLayout key = (LinearLayout)  findViewById(R.id.key);
		Button curser = null;
		// Gets the button that is located at location x on Layout key.
		curser = findViewByCoord(key, x);
		if (curser != null) {

			// If uppercase once a letter is added, the case is dropped back if
			// it is CAPS_LOCK
			if (currentKeyboard == 1 && CAPS_LOCK == false) {
				// if uppercase then lowercase
				currentKeyboard = 0;
				createKeyboard(key);
			}

			LinearLayout text = (LinearLayout)  findViewById(R.id.text);

			// creates button
			Button b = new Button(this);
			// 97 is the char a in ascii
			b.setText(curser.getText());
			b.setHeight(90);
			b.setTextColor(Color.parseColor("#7FFFFFFF"));
			b.setBackgroundColor(Color.parseColor("#7F070003"));
			b.setPadding(0, 0, 0, 0);
			b.setWidth(80);
			b.setClickable(false);

			// adds it to view and scrolls the bar once the view is completed
			text.addView(b);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					HorizontalScrollView textHolder = (HorizontalScrollView) findViewById(R.id.textHolder);
					textHolder.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
				}
			}, 25L);
		}
	}

	/**
	 * Removes a button at the position x from the text field
	 * 
	 * @param x
	 *            A int representation of the x location
	 */
	private void removeButton(float x) {
		LinearLayout text = (LinearLayout)  findViewById(R.id.text);
		Button curser = null;
		// finds Button based on x location and view
		curser = findViewByCoord(text, x);
		int foundButton = -1;
		if (curser != null) {
			foundButton = text.indexOfChild(curser);
			text.removeViewAt(foundButton);
		}
	}

	/**
	 * This clears the text from the text layout
	 */
	private String clearText() {
		LinearLayout text = (LinearLayout)  findViewById(R.id.text);
		StringBuilder word = new StringBuilder();
		for (int i = 0; i < text.getChildCount(); i++) {
			Button letter = (Button) text.getChildAt(i);
			word.append(letter.getText());
		}
		text.removeAllViews();
		return word.toString();
	}

	/**
	 * This recursive function finds the Button at position x on the Linear
	 * Layout provided
	 * 
	 * @param view
	 *            The LinearLayout that holds the buttons
	 * @param x
	 *            The int value of the x coordinate of the move event
	 * @return A button Object that is a copy of the button that was at position
	 *         x
	 */
	private Button findViewByCoord(LinearLayout view, float x) {
		Button retView = null;
		int size = view.getChildCount();
		int index = findIndex(view, x, 0, size);
		if (index >= 0) {
			retView = (Button) view.getChildAt(index);
		}
		return retView;
	}

	/**
	 * The recursive element demonstrates the binary search element to find the
	 * index
	 * 
	 * @param view
	 *            The view that contains the objects that are being searched
	 * @param x
	 *            An int representing the x Coord of the button
	 * @param indexMin
	 *            The low end
	 * @param indexMax
	 *            The high end
	 * @return the index within the view that holds the button at position x
	 */
	private int findIndex(LinearLayout view, float x, int indexMin, int indexMax) {
		int index = (indexMin + indexMax) / 2;
		Button curser = (Button) view.getChildAt(index);
		float left = curser.getLeft();
		float right = curser.getRight();
		if (x > left && x < right) {
			return index;
		} else if (indexMin > indexMax) {
			return -1;
		} else {
			if (x < left) {
				// go left
				return findIndex(view, x, indexMin, index - 1);
			} else if (x > right) {
				// go right
				return findIndex(view, x, index + 1, indexMax);
			}
		}
		return -1;
	}

	/**
	 * This Gesture is for the text field
	 * 
	 * @author charl_000 sets up gestures for text Current Gestures: -Up:clear
	 *         sentence -Down:deletes character -Left/Right: Scroll
	 *         -DoubleTap:adds spaces iff no space is present
	 */
	class MyTextGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			LinearLayout text = (LinearLayout)  findViewById(R.id.text);
			Button last = (Button) text.getChildAt(text.getChildCount() - 1);
			// checks if last item in view is a space
			if (!(last.getText().equals(" "))) {
				// if not add space
				Button b = new Button( getApplicationContext());
				b.setText(" ");
				b.setHeight(90);
				b.setTextColor(Color.parseColor("#7FFFFFFF"));
				b.setBackgroundColor(Color.parseColor("#7F070003"));
				b.setPadding(0, 0, 0, 0);
				b.setWidth(80);
				b.setClickable(false);
				// adds it to view and scrolls the bar once the view is
				// completed
				text.addView(b);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						HorizontalScrollView textHolder = (HorizontalScrollView) findViewById(R.id.textHolder);
						textHolder.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
					}
				}, 25L);
			}
			return true;
		}

		public boolean onFling(MotionEvent start, MotionEvent finish,
				float velocityX, float velocityY) {

			float horizontal = finish.getRawX() - start.getRawX();
			float vertical = finish.getRawY() - start.getRawY();

			Log.d("Text", "msg: start= (" + start.getX() + ", " + start.getY()
					+ ")");
			Log.d("Text",
					"msg: finish= (" + finish.getX() + ", " + finish.getY()
							+ ")");
			Log.d("Text", "msg: vertical= " + vertical);
			Log.d("Tewxt", "msg: horizontal= " + horizontal);
			if (Math.abs(vertical) >= Math.abs(horizontal)) {
				if (vertical < 0) {
					Log.d("Text", "msg: Up");
					@SuppressWarnings("unused")
					String result = clearText();
					Log.d("end", "sum: End word");
				} else if (vertical > 0) {
					Log.d("Text", "msg: Down");
					Log.d("Text", "msg:Function remove ");
					removeButton(start.getX());
				}
			} else {
				Log.d("Text", "msg:SCROLL");
				// if left right then do normal movement
				HorizontalScrollView x = (HorizontalScrollView) findViewById(R.id.textHolder);
				x.fling((int) velocityX * -1);
			}
			Log.d("Text", "msg:Done");
			return true;
		}
	};

	/**
	 * This inner class is the gesture detector for the keyboard
	 * 
	 * @author charl_000 Current Gestures: -Up:add letter -Down:changes keyboard
	 *         to utility -Left/Right: Scroll -DoubleTap:Capitalizes normal
	 *         characters
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (currentKeyboard == 0 && CAPS_LOCK == false) {
				// if lowercase then uppercase
				currentKeyboard = 1;
				LinearLayout key = (LinearLayout) findViewById(R.id.key);
				createKeyboard(key);
			} else if (currentKeyboard == 1 && CAPS_LOCK == false) {
				// if uppercase then lowercase
				currentKeyboard = 0;
				LinearLayout key = (LinearLayout) findViewById(R.id.key);
				createKeyboard(key);
			}
			// if utility then keyboard is not changed
			Log.d("MSG", "msg:Function shift key ");
			return true;
		}

		public boolean onFling(MotionEvent start, MotionEvent finish,
				float velocityX, float velocityY) {
			Log.d("MSG", "msg: start");

			// calculates the distance difference
			float horizontal = finish.getX() - start.getX();
			float vertical = finish.getY() - start.getY();

			Log.d("MSG", "msg: start= (" + start.getX() + ", " + start.getY()
					+ ")");
			Log.d("MSG",
					"msg: finish= (" + finish.getX() + ", " + finish.getY()
							+ ")");
			Log.d("MSG", "msg: vertical= " + vertical);
			Log.d("MSG", "msg: horizontal= " + horizontal);

			// if vertical swipe is larger then horizontal swipe then up/down
			// action is activated and vise versa
			if (Math.abs(vertical) >= Math.abs(horizontal)) {

				if (vertical < 0) {
					// creates a button based off the button that was swiped and
					// adds it to text
					Log.d("MSG", "msg:Up");
					Log.d("MSG", "msg:Function add ");
					createButton(start.getX());
				} else if (vertical > 0) {
					// changes keyboard between special characters and normal
					// characters
					Log.d("MSG", "msg:Down");
					if (currentKeyboard == 0 || currentKeyboard == 1) {
						// if keyboard is either upper/lowercase norm then
						// change to special characters
						currentKeyboard = 2;
						LinearLayout key = (LinearLayout) findViewById(R.id.key);
						createKeyboard(key);
					} else if (currentKeyboard == 2 && CAPS_LOCK == false) {
						// if character is special characters then lowercase
						// keyboard if CAPS_LOCK=FALSE
						currentKeyboard = 0;
						LinearLayout key = (LinearLayout) findViewById(R.id.key);
						createKeyboard(key);
					} else if (currentKeyboard == 2 && CAPS_LOCK == true) {
						// if character is special characters then uppercase
						// keyboard IF CAPS_LOCK=TRUE
						currentKeyboard = 1;
						LinearLayout key = (LinearLayout) findViewById(R.id.key);
						createKeyboard(key);
					}
				}
			} else {
				Log.d("MSG", "msg:SCROLL");
				// if left right then do normal movement
				HorizontalScrollView x = (HorizontalScrollView) findViewById(R.id.keyHolder);
				x.fling((int) velocityX * -1);
			}
			Log.d("MSG", "msg:Done");
			return true;
		}
	};
	
}
