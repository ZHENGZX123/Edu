package cn.kiway.yjhz.qrcode;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeUtils {
	private static final int BLACK = 0xff000000;
	 
    private static final int PADDING_SIZE_MIN = 10; // 最小留白长度, 单位: px
     
    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
    	Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); //容错率
		hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
 
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
 
        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;
 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (isFirstBlackPoint == false)
                    {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                        Log.d("createQRCode", "x y = " + x + " " + y);
                    }
                    pixels[y * width + x] = BLACK;
                }
            }
        }
 
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
         
        // 剪切中间的二维码区域，减少padding区域
        if (startX <= PADDING_SIZE_MIN) return bitmap;
 
        int x1 = startX - PADDING_SIZE_MIN;
        int y1 = startY - PADDING_SIZE_MIN;
        if (x1 < 0 || y1 < 0) return bitmap;
 
        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;
 
        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
         
        return bitmapQR;
    }

}

