package capstone.admk_brick.contents;

/**
 * Created by jaeho on 2015-05-10.
 */
public interface IContentManagerListener {
    public static final int CALLBACK_CONTENT_UPDATED = 1;

    public void OnContentCallback(int msgType, int arg0, int arg1, String arg2, String arg3, Object arg4);
}
