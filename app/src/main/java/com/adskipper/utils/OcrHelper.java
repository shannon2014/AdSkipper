package com.adskipper.utils;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;

// import com.google.mlkit.vision.common.InputImage;
// import com.google.mlkit.vision.text.TextRecognition;
// import com.google.mlkit.vision.text.TextRecognizer;
// import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class OcrHelper {

    private static final String TAG = "OcrHelper";
    // private static TextRecognizer recognizer;

    /**
     * Initialize OCR recognizer
     */
    public static void initialize() {
        /*
        if (recognizer == null) {
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        }
        */
    }

    /**
     * Extract text from an ImageView / ImageButton node by taking a screenshot
     * of its screen region and running ML Kit OCR on the cropped bitmap.
     * <p>
     * This is a <b>blocking</b> call (waits up to 3 seconds for the screenshot
     * and OCR to complete) and must therefore be invoked on a background thread.
     * <p>
     * Requires Android 11 (API 30) or higher because
     * {@link AccessibilityService#takeScreenshot} was introduced in API 30.
     *
     * @param node    the AccessibilityNodeInfo whose bounds define the crop area
     * @param service the AccessibilityService instance used to take the screenshot
     * @return the recognized text, or {@code null} on failure / timeout
     */
    public static String extractTextFromImage(AccessibilityNodeInfo node,
            AccessibilityService service) {
        /*
        if (node == null || service == null) {
            return null;
        }

        // Only handle ImageView / ImageButton nodes
        CharSequence className = node.getClassName();
        if (className == null)
            return null;
        String cls = className.toString();
        if (!cls.contains("ImageView") && !cls.contains("ImageButton")) {
            return null;
        }

        // takeScreenshot requires API 30+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Log.d(TAG, "takeScreenshot requires API 30+, skipping OCR");
            return null;
        }

        // Determine node bounds on screen
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        if (bounds.isEmpty() || bounds.width() < 10 || bounds.height() < 10) {
            return null;
        }

        // 1) Take a screenshot asynchronously, block until done
        AtomicReference<Bitmap> screenshotRef = new AtomicReference<>(null);
        CountDownLatch screenshotLatch = new CountDownLatch(1);

        try {
            service.takeScreenshot(
                    Display.DEFAULT_DISPLAY,
                    service.getMainExecutor(),
                    new AccessibilityService.TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(AccessibilityService.ScreenshotResult result) {
                            try {
                                Bitmap fullBitmap = Bitmap.wrapHardwareBuffer(
                                        result.getHardwareBuffer(),
                                        result.getColorSpace());
                                if (fullBitmap != null) {
                                    // Convert from hardware bitmap so we can crop
                                    Bitmap softBitmap = fullBitmap.copy(Bitmap.Config.ARGB_8888, false);
                                    fullBitmap.recycle();
                                    result.getHardwareBuffer().close();

                                    // Clamp bounds to bitmap dimensions
                                    int left = Math.max(0, bounds.left);
                                    int top = Math.max(0, bounds.top);
                                    int right = Math.min(softBitmap.getWidth(), bounds.right);
                                    int bottom = Math.min(softBitmap.getHeight(), bounds.bottom);

                                    if (right > left && bottom > top) {
                                        Bitmap cropped = Bitmap.createBitmap(
                                                softBitmap, left, top,
                                                right - left, bottom - top);
                                        screenshotRef.set(cropped);
                                    }
                                    softBitmap.recycle();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing screenshot", e);
                            } finally {
                                screenshotLatch.countDown();
                            }
                        }

                        @Override
                        public void onFailure(int errorCode) {
                            Log.e(TAG, "takeScreenshot failed, errorCode=" + errorCode);
                            screenshotLatch.countDown();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "takeScreenshot threw exception", e);
            return null;
        }

        // Wait for screenshot (max 2 seconds)
        try {
            if (!screenshotLatch.await(2, TimeUnit.SECONDS)) {
                Log.w(TAG, "Screenshot timed out");
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        Bitmap cropped = screenshotRef.get();
        if (cropped == null) {
            return null;
        }

        // 2) Run ML Kit OCR on the cropped bitmap, block until done
        if (recognizer == null) {
            initialize();
        }

        AtomicReference<String> textRef = new AtomicReference<>(null);
        CountDownLatch ocrLatch = new CountDownLatch(1);

        InputImage inputImage = InputImage.fromBitmap(cropped, 0);
        recognizer.process(inputImage)
                .addOnSuccessListener(visionText -> {
                    String result = visionText.getText();
                    if (result != null && !result.trim().isEmpty()) {
                        textRef.set(result.trim());
                        Log.d(TAG, "OCR result: " + result.trim());
                    }
                    ocrLatch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "OCR failed", e);
                    ocrLatch.countDown();
                });

        // Wait for OCR (max 1 second)
        try {
            if (!ocrLatch.await(1, TimeUnit.SECONDS)) {
                Log.w(TAG, "OCR timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        cropped.recycle();
        return textRef.get();
        */
        return null;
    }

    /**
     * Process a bitmap with OCR asynchronously (non-blocking).
     */
    public static void processImage(Bitmap bitmap, OcrCallback callback) {
        /*
        if (recognizer == null) {
            initialize();
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String text = visionText.getText();
                    callback.onSuccess(text);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "OCR failed", e);
                    callback.onFailure(e);
                });
        */
    }

    /**
     * Check if the extracted text matches known skip-button patterns.
     */
    public static boolean containsSkipPattern(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String lowerText = text.toLowerCase();

        String[] patterns = {
                "跳过", "skip", "关闭", "close", "×",
                "跳过广告", "skip ad", "广告", "秒后跳过",
                "点击跳过", "跳过此广告", "s后跳过"
        };

        for (String pattern : patterns) {
            if (lowerText.contains(pattern.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public interface OcrCallback {
        void onSuccess(String text);

        void onFailure(Exception e);
    }
}
