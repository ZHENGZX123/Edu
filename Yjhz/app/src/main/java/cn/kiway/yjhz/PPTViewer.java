package cn.kiway.yjhz;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.olivephone.office.TempFileManager;
import com.olivephone.office.powerpoint.DocumentSession;
import com.olivephone.office.powerpoint.DocumentSessionBuilder;
import com.olivephone.office.powerpoint.DocumentSessionStatusListener;
import com.olivephone.office.powerpoint.IMessageProvider;
import com.olivephone.office.powerpoint.ISystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidMessageProvider;
import com.olivephone.office.powerpoint.android.AndroidSystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidTempFileStorageProvider;
import com.olivephone.office.powerpoint.view.PersentationView;
import com.olivephone.office.powerpoint.view.SlideShowNavigator;
import com.olivephone.office.powerpoint.view.SlideView;

public class PPTViewer extends RelativeLayout implements
		DocumentSessionStatusListener {
	ProgressBar pb;
	RelativeLayout.LayoutParams params;
	private DocumentSession session;
	PersentationView slide;
	String path;
	Context ctx;
	Activity act;
	private SlideShowNavigator navitator;
	private int currentSlideNumber;
	final int SLIDE = 1233;

	public int getTotalSlides() {
		if ((this.session != null) && (this.session.getPPTContext() != null)) {
			return this.navitator.getSlideCount();
		}
		return -1;
	}

	void log(Object log) {
		Log.d("rex", log.toString());
	}

	@SuppressLint("ShowToast")
	void toast(Object msg) {
		Toast.makeText(this.act, msg.toString(), 0).show();
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void loadPPT(Activity act, String path) {
		setPath(path);
		loadPPT(act);
	}

	public void loadPPT(Activity act) {
		this.act = act;
		try {
			IMessageProvider msgProvider = new AndroidMessageProvider(this.ctx);
			TempFileManager tmpFileManager = new TempFileManager(
					new AndroidTempFileStorageProvider(this.ctx));
			ISystemColorProvider sysColorProvider = new AndroidSystemColorProvider();
			this.session = new DocumentSessionBuilder(new File(this.path))
					.setMessageProvider(msgProvider)
					.setTempFileManager(tmpFileManager)
					.setSystemColorProvider(sysColorProvider)
					.setSessionStatusListener(this).build();
			this.session.startSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PPTViewer(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		this.ctx = ctx;
		this.pb = new ProgressBar(ctx);
		this.slide = new PersentationView(ctx, attr);
		addView(this.slide);
		this.slide.notifyScale(75.0F / 100.0D);
	}

	public void onDocumentException(Exception arg0) {
	}

	public void onDocumentReady() {
		this.act.runOnUiThread(new Runnable() {
			public void run() {
				PPTViewer.this.navitator = new SlideShowNavigator(
						PPTViewer.this.session.getPPTContext());
				PPTViewer.this.currentSlideNumber = (PPTViewer.this.navitator
						.getFirstSlideNumber() - 1);
				PPTViewer.this.next();
				PPTViewer.this.pb.setVisibility(4);
				PPTViewer.this.slide.setVisibility(0);
			}
		});
	}

	public void onSessionEnded() {
	}

	public void onSessionStarted() {
	}

	public void navigateTo(int slideNumber) {
		log(Integer.valueOf(slideNumber));
		SlideView slideShow = this.navitator.navigateToSlide(
				this.slide.getGraphicsContext(), slideNumber);
		this.slide.setContentView(slideShow);
	}

	public void next() {
		if (this.navitator != null)
			if (this.navitator.getFirstSlideNumber()
					+ this.navitator.getSlideCount() - 1 > this.currentSlideNumber)
				navigateTo(++this.currentSlideNumber);
			else
				toast("这是最后一页了");
	}

	public void prev() {
		if (this.navitator != null)
			if (this.navitator.getFirstSlideNumber() < this.currentSlideNumber)
				navigateTo(--this.currentSlideNumber);
			else
				toast("这是第一页了");
	}
}
