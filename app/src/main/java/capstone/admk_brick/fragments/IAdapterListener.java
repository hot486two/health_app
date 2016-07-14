package capstone.admk_brick.fragments;

/**
 * Created by jaeho on 2015-05-10.
 */
public interface IAdapterListener {
    public static final int CALLBACK_xxx = 1;

    public void OnAdapterCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4);
}
