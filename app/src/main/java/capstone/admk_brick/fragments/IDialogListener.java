package capstone.admk_brick.fragments;

/**
 * Created by jaeho on 2015-05-10.
 */
public interface IDialogListener {
    public static final int CALLBACK_ENABLE_MESSAGE = 1;
    public static final int CALLBACK_ENABLE_PACKAGE = 2;

    public static final int CALLBACK_CLOSE = 1000;

    public void OnDialogCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4);
}
