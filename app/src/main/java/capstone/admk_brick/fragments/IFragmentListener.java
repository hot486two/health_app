package capstone.admk_brick.fragments;

/**
 * Created by jaeho on 2015-05-10.
 */
public interface IFragmentListener {
    public static final int CALLBACK_RUN_IN_BACKGROUND = 1;

    public void OnFragmentCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4);
}
