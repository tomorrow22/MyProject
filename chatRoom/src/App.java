import com.itheima.domain.User;
import com.itheima.domain.FriendPublicInfo;
import com.itheima.ui.AppJFrame;
import com.itheima.ui.ChatJFrame;
import com.itheima.ui.LoginJFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        //主入口
        new LoginJFrame();
        //new RegexJFrame();

        HashMap<String, ArrayList<String>> chat = new HashMap<>();

        ArrayList<String> text = new ArrayList<>();
        text.add("测试2:好啊");
        text.add("测试2:那今晚不见不散");
        text.add("测试2:那今晚不见不散");
        text.add("测试2:那今晚不见不散");
        text.add("测试2:那今晚不见不散");
        text.add("明天再说吧:那就这么说定了!");
        text.add("明天再说吧:那就这么说定了!");
        text.add("明天再说吧:那就这么说定了!");
        text.add("明天再说吧:那就这么说定了!");
        text.add("明天再说吧:拜拜");
        text.add("明天再说吧:拜拜");
        text.add("明天再说吧:拜拜");
        text.add("明天再说吧:拜拜");
        chat.put("1",new ArrayList<>());
        chat.put("2",text);
        chat.put("3",text);
        chat.put("4",text);
        chat.put("5",text);
        chat.put("6",text);
        chat.put("7",text);
        chat.put("8",text);

        //头像
        FileInputStream fis = new FileInputStream("images\\user_bright.png");
        byte[] bytes = new byte[1024 * 1024 * 5];
        int read = fis.read(bytes);
        fis.close();
        //ImageIO.read：读取
        //ByteArrayInputStream：读取的流
        //getScaledInstance：生成了这张图片的缩放版本。返回一个新Image对象
        ImageIcon icon = new ImageIcon(ImageIO.read(new ByteArrayInputStream(bytes)).getScaledInstance(40, 30, Image.SCALE_SMOOTH));

        HashMap<String, FriendPublicInfo> friendMap = new HashMap<>();
        friendMap.put("1",new FriendPublicInfo("测试1","1",icon,"吃吃喝喝，人生快乐！",true));
        friendMap.put("2",new FriendPublicInfo("测试2","2",icon,"这是什么？",true));
        friendMap.put("3",new FriendPublicInfo("测试3","3",icon,"红红火火恍恍惚惚",true));
        friendMap.put("4",new FriendPublicInfo("测试4","4",icon,"红红火火恍恍惚惚",true));
        friendMap.put("5",new FriendPublicInfo("测试5","5",icon,"红红火火恍恍惚惚",true));
        friendMap.put("6",new FriendPublicInfo("测试6","6",icon,"红红火火恍恍惚惚",true));
        friendMap.put("7",new FriendPublicInfo("测试7","7",icon,"红红火火恍恍惚惚",true));
        friendMap.put("8",new FriendPublicInfo("测试8","8",icon,"红红火火恍恍惚惚",true));

        User user = new User("1", "明天再说吧", "123", "127.0.0.1", "15078680291", icon, "吃吃喝喝，人生快乐！", null, friendMap, chat, new HashMap<>(), true, false);
        //new ChatJFrame(chat.get("2"),user,user.getFriendList().get("2"));

        //new AppJFrame(user);
    }
}
