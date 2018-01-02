
//         		         _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      O\  =  /O
//                   ____/`---'\____
//                 .'  \\|     |//  `.
//                /  \\|||  :  |||//  \
//               /  _||||| -:- |||||-  \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |   |
//               \  .-\__  `-`  ___/-. /
//             ___`. .'  /--.--\  `. . __
//          ."" '<  `.___\_<|>_/___.'  >'"".
//         | | :  ` - `.;`\ _ /`;.`/ - ` : | |
//         \  \ `-.   \_ __\ /__ _/   .-` /  /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//              佛祖保佑---XJX---永无BUG

package xjx;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;

public class Launcher {
	public static void main(String args[]){
		new MainWindow();
	}
}

class Box{
    JButton button;
	int expn;
	boolean flag;
}

class Boxinfo{//history present 专用
	int expn;
	boolean flag;
}

class MainWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3733117110572934516L;
	private static int row = 4,col = 4;//行列默认都是4
	private static Box[][] box = new Box[row][col];
	private static Boxinfo[][] history = new Boxinfo[row][col];//用来记录方块的历史布局
	private static Boxinfo[][] present = new Boxinfo[row][col];//用来记录方块的当前布局
	private static boolean swap = false;//判断 present 是否 赋给 history
	private static boolean show = true;//判断开始是否继续上次记录
	private Color[] color = new Color[12];//2048 每种数值的方块一种颜色
	private JPanel J2;
	private JTextField J12,J14;
	private JLabel showtime;
	private JRadioButtonMenuItem modeItems[];
	private ButtonGroup modeGroup;
	private int count = 0;//记录当前得分,初始得分为0
	private int best_score = 0;//记录最佳得分，初始化为0，每次开始时重txt中读取数据刷新该值
	private String temp = "";
	//定义变量存储时分秒
	private int hour =0;
	private int min =0;
	private int sec =0 ;
	private boolean isRun = true;
    
	public MainWindow(){
		super("2048 By XJX");
		Image img = Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
		setIconImage(img);
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(500,600);
		setFocusable(true);
		
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		setLayout(new BorderLayout());
		setResizable(false);
		JPanel J1 = new JPanel();
		J1.setBackground(Color.gray);
		J2 = new JPanel();
		J1.setLayout(new GridLayout(1,4,2,2));
		J2.setLayout(new GridLayout(row,col,10,10));
		J2.setFocusable(true);
		JPanel J3 = new JPanel();
		J3.setLayout(new GridLayout(1,2,2,2));
		JButton J31 = new JButton("RESTART");
		JButton J32 = new JButton("UNDO");
		
		//重置模块监听器
		J31.addActionListener( 
				new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int result=JOptionPane.showConfirmDialog(null, "Are you sure to restart the game?", "Information", JOptionPane.YES_NO_OPTION);
					if(result==JOptionPane.YES_NO_OPTION)
					{
						for(int i = 0;i < row;i++)
							for(int j = 0;j < col;j++)
							{
								box[i][j].flag = false;
								box[i][j].expn = 0;
								box[i][j].button.setVisible(false);
							}
						best_score = (best_score > count )? best_score:count;//判断当前得分是否高于历史最佳得分，刷新最佳得分
						temp += best_score;
						J14.setText(temp);
						temp = "";
						count = 0;
						J12.setText("0");
						//重新开始，随机产生两个方块
						isRun = false;
						hour =0;
					    min =0;
					    sec =0 ;
						isRun = true;
						produceRandom();
						produceRandom();
						history = record_box();//记录初始化的棋盘记录到history数组中
						present = record_box();//记录初始化的棋盘记录到present数组中
						swap = false;//记得重置
						J31.setFocusable(false);
						J32.setFocusable(false);
						setFocusable(true);
					}
				}
			}
		);
		
		//撤回到上一步模块
		J32.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						for(int i = 0;i < row;i++)
							for(int j = 0;j < col;j++)
								{
									box[i][j].expn = history[i][j].expn;
									box[i][j].flag = history[i][j].flag;
									box[i][j].button.setVisible(box[i][j].flag);
									temp += binary(box[i][j].expn);
									box[i][j].button.setText(temp);
									box[i][j].button.setBackground(color[box[i][j].expn]);
									temp = "";
								}
						swap = false;
						history = record_box();
						present = record_box();
						J32.setFocusable(false);
						J31.setFocusable(false);
						setFocusable(true);
					}
				}
		);
		
		J3.add(J31);
		J3.add(J32);
		JPanel J4 = new JPanel();
		J4.setLayout(new GridLayout(2,1,2,2));
		J4.add(J1);
		JPanel timePanel = new JPanel();
		timePanel.setBackground(Color.white);
		timePanel.setLayout(new GridLayout(1,2,2,2));
		JLabel timeLabel = new JLabel("                     Time used:");
		showtime = new JLabel("   00:00:00");
		showtime.setBackground(Color.gray);
		timePanel.add(timeLabel);
		timePanel.add(showtime);
		J4.add(timePanel);
		JLabel J11 = new JLabel("    SCORE:");
		J12 = new JTextField("0");
		J12.setBackground(Color.cyan);
		J12.setEditable(false);
		JLabel J13 = new JLabel("     BEST:");
		J14 = new JTextField("0");
		J14.setBackground(Color.cyan);
		J14.setEditable(false);
		J11.setFont(new Font("微软雅黑",Font.BOLD,20));
		J12.setFont(new Font("微软雅黑",Font.BOLD,20));
		J13.setFont(new Font("微软雅黑",Font.BOLD,20));
		J14.setFont(new Font("微软雅黑",Font.BOLD,20));
		J31.setFont(new Font("微软雅黑",Font.BOLD,20));
		J32.setFont(new Font("微软雅黑",Font.BOLD,20));
		timeLabel.setFont(new Font("微软雅黑",Font.BOLD,20));
		showtime.setFont(new Font("微软雅黑",Font.BOLD,20));
		J1.add(J11);
		J1.add(J12);
		J1.add(J13);
		J1.add(J14);
		add(J4,BorderLayout.NORTH);
		add(J2,BorderLayout.CENTER);
		add(J3,BorderLayout.SOUTH);
		for(int i = 0;i < row;i++)
			for(int j = 0; j < col;j++)
			{
				box[i][j] = new Box();
				box[i][j].expn = 0;//指数初始化为0
				box[i][j].flag = false;//初始化为不可见
				box[i][j].button = new JButton();
				box[i][j].button.setFont(new Font("微软雅黑",Font.BOLD,40));
				box[i][j].button.setVisible(false);
				J2.add(box[i][j].button);
			}
		setLocationRelativeTo(null);//显示在屏幕中央
		setVisible(true);
		addKeyListener(new KeyMonitor());
		color[1] = Color.yellow;
		color[2] = Color.blue;
		color[3] = Color.green;
		color[4] = Color.gray;
		color[5] = Color.red;
		color[6] = Color.pink;
		color[7] = Color.magenta;
		color[8] = Color.orange;
		color[9] = new Color(128,0,128);
		color[10] = Color.white;
		color[11] = new Color(0,0,0);
		
		//菜单栏部分
		JMenuBar bar = new JMenuBar();
		bar.setBackground(Color.white);
		setJMenuBar(bar);
		JMenu fileMenu = new JMenu("File");
		JMenu setMenu = new JMenu("Settings");
		JMenu modeMenu = new JMenu("Modes");
		bar.add(fileMenu);
		bar.add(setMenu);
		setMenu.add(modeMenu);
		
		//选择模式
		String modes[] = {"4 * 4","5 * 5","6 * 6"};
		modeItems = new JRadioButtonMenuItem[modes.length];
		modeGroup = new ButtonGroup();
		for(int i = 0;i < modes.length;i++)
		{
			modeItems[i] = new JRadioButtonMenuItem(modes[i]);
			modeMenu.add(modeItems[i]);
			modeGroup.add(modeItems[i]);
			modeItems[i].addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e){
							//停止计时
							isRun = false;
							String temp = "";
							for(int i = 0;i < modeItems.length;i++)
							{
								if(modeItems[i].isSelected()){
									temp = modes[i];
									row = temp.charAt(0) - 48;
									col = temp.charAt(temp.length()-1) - 48;
									System.out.println("row = " + row);
									System.out.println("col = " + col);
								}
							}
							//重新开始，随机产生两个方块
							J2.setVisible(false);
							J2 = new JPanel();
							J2.setFocusable(true);
							add(J2,BorderLayout.CENTER);
							box = new Box[row][col];
							for(int i = 0;i < row;i++)
								for(int j = 0; j < col;j++)
								{
									box[i][j] = new Box();
									box[i][j].expn = 0;//指数初始化为0
									box[i][j].flag = false;//初始化为不可见
									box[i][j].button = new JButton();
									box[i][j].button.setFont(new Font( "微软雅黑",Font.BOLD, 120/row) );
									box[i][j].button.setVisible(false);
									J2.add(box[i][j].button);
								}
							history = new Boxinfo[row][col];//用来记录方块的历史布局
							present = new Boxinfo[row][col];//用来记录方块的当前布局
							J2.setLayout(new GridLayout(row,col,10,10));
							//重新开始计时
							hour =0;
						    min =0;
						    sec =0 ;
							isRun = true;
							produceRandom();
							produceRandom();
							history = record_box();//记录初始化的棋盘记录到history数组中
							present = record_box();//记录初始化的棋盘记录到present数组中
							swap = false;//记得重置
						}
					}
			);
		}
		
		
		//File菜单栏模块
		JMenuItem aboutItem = new JMenuItem("About");
		JMenuItem helpItem = new JMenuItem("Help");
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(aboutItem);
		fileMenu.add(helpItem);
		fileMenu.add(exitItem);

		aboutItem.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent e){
					new About();
				}
			}
		);
		helpItem.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						new Help();
					}
				}
		);
		//请注意，此按钮是直接退出，退出时不会记录你的得分和历史记录
		exitItem.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent e){
					setVisible(false);
					System.exit(0);
				}
			}
		);
		//确认是否退出程序
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				 int result=JOptionPane.showConfirmDialog(null, "Are you sure to exit the game?", "Information", JOptionPane.YES_NO_OPTION);
				 if(result==JOptionPane.YES_NO_OPTION)
				 {
					try {
						//每次退出程序之前保存最佳得分，以便下一次重新启动时使用
						if(J14.getText().equals(""))//如果退出的时候BEST显示为空，说明当前best为0
						{
							temp += count;
//							write(temp,"D:\\MyEclipse 2016 CI\\2048game\\src\\best_score.txt");
							write(temp,"best_score.txt");
							temp = "";
						}
						else
						{
							temp = J14.getText();
							best_score = StringToInt(temp);
							best_score = (best_score > count)? best_score:count;
							temp = "";
							temp += best_score;
//							write(temp,"D:\\MyEclipse 2016 CI\\2048game\\src\\best_score.txt");
							write(temp,"best_score.txt");
							temp = "";
						}
						//每次退出程序之前保存退出之前的记录，以便下一次重新启动时使用
						temp += "$ " + row + " " + col + " ";
						for(int i = 0;i < row;i++)
							for(int j = 0;j < col;j++)
							{
								temp += box[i][j].expn + " ";
							}
						
//						write(temp,"D:\\MyEclipse 2016 CI\\2048game\\src\\history.dat");
						write(temp,"history.dat");
						temp = "";
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					setVisible(false);
					System.exit(0);
				 }
				 else
				 {
					 setVisible(true);
				 }
			}
		});
		
		/*---------------------------游戏启动------------------------------*/
		try {
			read_history();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		if(show)
		{
			int result=JOptionPane.showConfirmDialog(null, "是否继续上次游戏?", "Information", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_NO_OPTION)
			{
				for(int i = 0; i < row;i++)
					for(int j = 0;j < col;j++)
					{
						box[i][j].button.setVisible(box[i][j].flag);
					}
				modeItems[row-4].setSelected(true);
			}
			else
			{
				for(int i = 0;i < row;i++)
					for(int j = 0;j < col;j++)
					{
						box[i][j].flag = false;
						box[i][j].expn = 0;
						box[i][j].button.setVisible(false);
					}
				//程序开始运行时随机产生2个方块
				modeItems[0].setSelected(true);//默认是4*4模式
				produceRandom();
				produceRandom();
			}
		}
		else
		{
			for(int i = 0;i < row;i++)
				for(int j = 0;j < col;j++)
				{
					box[i][j].flag = false;
					box[i][j].expn = 0;
					box[i][j].button.setVisible(false);
				}
			//程序开始运行时随机产生2个方块
			produceRandom();
			produceRandom();
		}
		history = record_box();//记录初始化的棋盘记录到history数组中
		present = record_box();//记录初始化的棋盘记录到present数组中
		
		//计时器开始计时
		new Timer();
		
		//开始时读取txt中的内容，设为best的初始值
		try {
			temp = read_best();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		J14.setText(temp);
		best_score = StringToInt(temp);
		temp = "";
	}
	
	void print(Boxinfo[][] b){
		for(int i = 0;i < row;i++)
		{
			for(int j = 0;j < col;j++)
				System.out.print(b[i][j].flag + " ");
			System.out.println();
		}
	}
	
	Boxinfo[][] record_history(Boxinfo[][] b){
		Boxinfo[][] temp = new Boxinfo[row][col];
		for(int i = 0;i < row;i++)
			for(int j = 0;j < col;j++)
			{
				temp[i][j] = new Boxinfo();
				temp[i][j].flag = b[i][j].flag;
				temp[i][j].expn = b[i][j].expn;
			}
		return temp;
	}
	
	Boxinfo[][] record_box(){
		Boxinfo[][] temp = new Boxinfo[row][col];
		for(int i = 0;i < row;i++)
			for(int j = 0;j < col;j++)
			{
				temp[i][j] = new Boxinfo();
				temp[i][j].flag = box[i][j].flag;
				temp[i][j].expn = box[i][j].expn;
			}
		return temp;
	}
	
	String read_best() throws IOException {
		String best = "0";
//		FileInputStream fis = new FileInputStream("D:\\MyEclipse 2016 CI\\2048game\\src\\best_score.txt");
		FileInputStream fis = new FileInputStream("best_score.txt");
		BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
		try
		{
			best = dis.readLine();
		}
		finally
		{
			dis.close();
		}
		return best;
	}
	
	void read_history() throws IOException {
		String s = "";
		int ptr = 0;
		String temp = "";
//		FileInputStream fis = new FileInputStream("D:\\MyEclipse 2016 CI\\2048game\\src\\history.dat");
		FileInputStream fis = new FileInputStream("history.dat");
		BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
		try
		{
			s = dis.readLine();
			if(s.equals(""))//history内容为空
			{
				show = false;
			}
			else
			{
				if(s.charAt(ptr) != '$') show = false;
				else//第一个符号为$表示存在上次记录
				{
					//判断上次记录里是否存在非法字符
					for(int i = 2;i < s.length();i++)
					{
						if((s.charAt(i) >= 48 && s.charAt(i) <= 57) || s.charAt(i) == 32) {}
						else
						{
							show = false;
							return;
						}
					}
				}
			}
			
			if(show)
			{
				ptr += 2;
				row = s.charAt(ptr) - 48;
				ptr += 2;
				col = s.charAt(ptr) - 48;
				ptr += 2;
				if(row != 4 || col != 4)//历史记录中的row或者col不等于程序初始值
				{
					J2.setVisible(false);
					J2 = new JPanel();
					J2.setFocusable(true);
					add(J2,BorderLayout.CENTER);
					box = new Box[row][col];
					for(int i = 0;i < row;i++)
						for(int j = 0; j < col;j++)
						{
							box[i][j] = new Box();
							box[i][j].expn = 0;//指数初始化为0
							box[i][j].flag = false;//初始化为不可见
							box[i][j].button = new JButton();
							box[i][j].button.setFont(new Font( "微软雅黑",Font.BOLD, 120/row) );
							box[i][j].button.setVisible(false);
							J2.add(box[i][j].button);
						}
					history = new Boxinfo[row][col];//用来记录方块的历史布局
					present = new Boxinfo[row][col];//用来记录方块的当前布局
					J2.setLayout(new GridLayout(row,col,10,10));
				}
				for(int i = 0;i < row;i++)
					for(int j = 0;j < col;j++)
					{
						if(s.charAt(ptr+1) != ' ')
						{
							temp += s.charAt(ptr);
							temp += s.charAt(ptr+1);
							box[i][j].expn = StringToInt(temp);
							temp = "";
							box[i][j].flag = true;
							temp += binary(box[i][j].expn);
							box[i][j].button.setText(temp);
							temp = "";
							box[i][j].button.setBackground(color[box[i][j].expn]);
							ptr += 3;
						}
						else
						{
							box[i][j].expn = s.charAt(ptr) - 48;
							if(box[i][j].expn > 0)
							{
								box[i][j].flag = true;
								temp += binary(box[i][j].expn);
								box[i][j].button.setText(temp);
								temp = "";
								box[i][j].button.setBackground(color[box[i][j].expn]);
							}
							else
							{
								box[i][j].flag = false;
							}
							ptr += 2;
						}
					}
			}
		}
		finally
		{
			dis.close();
		}
	}
	
	void write(String s,String filepath) throws IOException {
		FileOutputStream fos = new FileOutputStream(filepath);
		DataOutputStream dos = new DataOutputStream(fos);
		try
		{
			dos.writeBytes(s);
		}
		finally
		{
			dos.close();
		}
	}

	int StringToInt(String s){
		int a = 0;
		for(int i = 0;i < s.length();i++)
		{
			a += ( (int)s.charAt(i) - 48 ) * decimal(s.length()-1-i);
		}
		return a;
	}

    int decimal(int n){ //计算10的n次方
		int s = 1;
		for(int i = 0;i < n;i++)
		{
			s *= 10;
		}
		return s;
	}
	
	int binary(int n){ //计算2的n次方
		int s = 1;
		for(int i = 0;i < n;i++)
		{
			s *= 2;
		}
		return s;
	}
	//判断是否赢了的方法
	boolean success(){
		boolean flag = false;
		for(int i = 0;i < row;i++)
			for(int j = 0;j < col;j++)
				if(box[i][j].flag)
				{
					if(box[i][j].expn == 11)//是否存在某个方块的值为2^11 = 2048
					{
						flag = true;
						return flag;
					}
				}
		return flag;
	}
	//产生两个随机方块的方法
	public void produceRandom(){
		Random rand = new Random();
		String s[] = new String[2];
		s[0] = "2";
		s[1] = "4";
		int x = rand.nextInt(row);
		int y = rand.nextInt(col);
		int z = rand.nextInt(2)+1;//z为1或者2
		while(box[x][y].flag){
			x = rand.nextInt(row);
			y = rand.nextInt(col);
			if(isfull())//满了 
			{
				System.out.println("FULL!");
				return;
			}
		}
		box[x][y].flag = true;
		box[x][y].expn = z;//随机生成的都是2或者4
		box[x][y].button.setText(s[z-1]);
		box[x][y].button.setBackground(color[z]);
		box[x][y].button.setVisible(true);
		System.out.print(x + " , ");
		System.out.println(y);
	}
	//判断布局是否满的方法
	public boolean isfull()
	{
		boolean tag = true;
		for(int i = 0;i < row;i++)
			for(int j = 0;j < col;j++)
				if(!box[i][j].flag)
				{
					tag = false;
					break;
				}
		return tag;
	}
	//判断是否可以移动的方法
	public boolean movable(String s){
		boolean tag = false;//默认不可移动
		if(s == "L")//向左移动
		{
			for(int i = 0;i < row;i++)
			{
				for(int j = col-1;j >= 1;j--)//每一行从右往左找
				{
					if(box[i][j].flag)//如果找到一个存在的方块
					{
						if(!box[i][j-1].flag) //判断它左边是否有空格
						{
							tag = true;//可以移动
							break;
						}
						else //左边不为空格的时候，判断是否可以合并
						{
							if(box[i][j-1].expn == box[i][j].expn) tag = true;
						}
					}
				}
				if(tag) break;
			}
		}
		else if(s == "R")
		{
			for(int i = 0;i < row;i++)
			{
				for(int j = 0;j < col-1;j++)//每一行从左往右找
				{
					if(box[i][j].flag)//如果找到一个存在的方块
					{
						if(!box[i][j+1].flag) //判断它右边是否有空格
						{
							tag = true;//可以移动
							break;
						}
						else //右边不为空格的时候，判断是否可以合并
						{
							if(box[i][j+1].expn == box[i][j].expn) tag = true;
						}
					}
				}
				if(tag) break;
			}
		}
		else if(s == "U")
		{
			for(int i = 0;i < row;i++)
			{
				for(int j = col-1;j >= 1;j--)//每一列从下往上找
				{
					if(box[j][i].flag)//如果找到一个存在的方块
					{
						if(!box[j-1][i].flag) //那么判断它上边是否有空格
						{
							tag = true;//可以移动
							break;
						}
						else //上边不为空格的时候，判断是否可以合并
						{
							if(box[j-1][i].expn == box[j][i].expn) tag = true;
						}
					}
				}
				if(tag) break;
			}
		}
		else if(s == "D")
		{
			for(int i = 0;i < row;i++)
			{
				for(int j = 0;j < col-1;j++)//每一列从上往下找
				{
					if(box[j][i].flag)//如果找到一个存在的方块
					{
						if(!box[j+1][i].flag) //判断它下边是否有空格
						{
							tag = true;//可以移动
							break;
						}
						else //下边不为空格的时候，判断是否可以合并
						{
							if(box[j+1][i].expn == box[j][i].expn) tag = true;
						}
					}
				}
				if(tag) break;
			}
		}
		return tag;
	}
	
	//重新开始
	public void Restart(){
		if(success())//每次移动完都判断是否赢了
		{
			isRun = false;
			int result=JOptionPane.showConfirmDialog(null, "恭喜你赢了！是否继续？", "Information", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_NO_OPTION)
			{
				for(int i = 0;i < row;i++)
					for(int j = 0;j < col;j++)
					{
						box[i][j].flag = false;
						box[i][j].expn = 0;
						box[i][j].button.setVisible(false);
					}
				best_score = (best_score > count )? best_score:count;//判断当前得分是否高于历史最佳得分，刷新最佳得分
				temp += best_score;
				J14.setText(temp);
				temp = "";
				count = 0;
				J12.setText("0");
				//重新开始，随机产生两个方块
				hour =0;
			    min =0;
			    sec =0 ;
				isRun = true;
				produceRandom();
				produceRandom();
				history = record_box();//记录初始化的棋盘记录到history数组中
				present = record_box();//记录初始化的棋盘记录到present数组中
				swap = false;//记得重置
			}
		}
		else
		{
			produceRandom();
			if(swap)
			{
				history = record_history(present);
			}
			present = record_box();//每次移动完把当前棋盘记录到present数组中
			swap = true;//swap变为true表示不是开始后的第一次移动
			
		}
	}
	
	//死亡
	public void Die(){
		
		if(isfull() && !movable("R") && !movable("U") && !movable("D"))//当前的布局已满而且各个方向都不可移动
		{
			isRun = false;
			int result=JOptionPane.showConfirmDialog(null, "对不起，你输了！是否重来一局？", "Information", JOptionPane.YES_NO_OPTION);
			if(result==JOptionPane.YES_NO_OPTION)
			{
				for(int i = 0;i < row;i++)
					for(int j = 0;j < col;j++)
					{
						box[i][j].flag = false;
						box[i][j].expn = 0;
						box[i][j].button.setVisible(false);
					}
				best_score = (best_score > count )? best_score:count;//判断当前得分是否高于历史最佳得分，刷新最佳得分
				temp += best_score;
				J14.setText(temp);
				temp = "";
				count = 0;
				J12.setText("0");
				//重新开始，随机产生两个方块
				hour =0;
			    min =0;
			    sec =0 ;
				isRun = true;
				produceRandom();
				produceRandom();
				history = record_box();//记录初始化的棋盘记录到history数组中
				present = record_box();//记录初始化的棋盘记录到present数组中
				swap = false;//记得重置
			}
		}
	}
	
	//实现移动功能的类	
	class KeyMonitor extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
			int x = 0;
			String s = "";
			boolean tag = true;
			if(key == KeyEvent.VK_LEFT)
			{
				x = 0;
				if( movable("L") )//可以往左移动
				{
					for(int i = 0;i < row;i++)
						for(int j = 0;j < col;j++)
						{
							if( box[i][j].flag )
							{
								for(int k = j+1;k < col;k++)
								{
									if( box[i][k].flag )
									{
										if(box[i][j].expn == box[i][k].expn)//可以合并
										{
											box[i][j].expn++;
											count += binary(box[i][j].expn);
											box[i][k].flag = false;
											box[i][k].expn = 0;
											j = k;
											new AePlayWave("merge.wav").start();
											tag = false;
											break;
										}
										else
										{
											break;
										}
									}
								}
							}
						}
					
					for(int i = 0;i < row;i++)
					{
						for(int j = 0;j < col;j++)
						{
							if(box[i][j].flag)
							{
								box[i][x].expn = box[i][j].expn;
								box[i][x].flag = true;
								box[i][x].button.setVisible(true);
								s += binary(box[i][x].expn);
								box[i][x].button.setText(s);
								box[i][x].button.setBackground(color[box[i][x].expn]);
								x++;
								s = "";//清空s
							}
						}
						for(int k = x; k < col;k++)
						{
							box[i][k].flag = false;
							box[i][k].expn = 0;
							box[i][k].button.setVisible(false);
						}
						x = 0;
					}
					
					//刷新当前得分
					s += count;
					J12.setText(s);
					s = "";
					//刷新最佳得分
					best_score = (best_score > count)? best_score:count;
					s += best_score;
					J14.setText(s);
					s = "";
					if(tag)
					{
						new AePlayWave("move.wav").start();
					}
					Restart();
				}
				else
				{
					Die();
				}
			}
			else if(key == KeyEvent.VK_RIGHT)
			{
				x = row-1;
				if( movable("R") )//可以往右移动
				{
					for(int i = 0;i < row;i++)
						for(int j = col-1;j >=0 ;j--)
						{
							if( box[i][j].flag )
							{
								for(int k = j-1;k >= 0;k--)
								{
									if( box[i][k].flag )
									{
										if(box[i][j].expn == box[i][k].expn)
										{
											box[i][j].expn++;
											count += binary(box[i][j].expn);
											box[i][k].flag = false;
											box[i][k].expn = 0;
											j = k;
											new AePlayWave("merge.wav").start();
											tag = false;
											break;
										}
										else
										{
											break;
										}
									}
								}
							}
						}
					for(int i = 0;i < row;i++)
					{
						for(int j = col-1;j >= 0;j--)
						{
							if(box[i][j].flag)
							{
								box[i][x].expn = box[i][j].expn;
								box[i][x].flag = true;
								box[i][x].button.setVisible(true);
								s += binary(box[i][x].expn);
								box[i][x].button.setText(s);
								box[i][x].button.setBackground(color[box[i][x].expn]);
								x--;
								s = "";
							}
						}
						for(int k = x;k >= 0;k--)
						{
							box[i][k].flag = false;
							box[i][k].expn = 0;
							box[i][k].button.setVisible(false);
						}
						x = row-1;
					}
					s += count;
					J12.setText(s);
					s = "";
					//刷新最佳得分
					best_score = (best_score > count)? best_score:count;
					s += best_score;
					J14.setText(s);
					s = "";
					
					if(tag)
					{
						new AePlayWave("move.wav").start();
					}
					Restart();
				}
				else
				{
					Die();
				}
			}
			else if(key == KeyEvent.VK_UP)
			{
				x = 0;
				if( movable("U") )//可以往上移动
				{
					for(int i = 0;i < row;i++)
						for(int j = 0;j < col;j++)
						{
							if( box[j][i].flag )
							{
								for(int k = j+1;k < row;k++)
								{
									if( box[k][i].flag )
									{
										if(box[j][i].expn == box[k][i].expn)
										{
											box[j][i].expn++;
											count += binary(box[j][i].expn);
											box[k][i].flag = false;
											box[k][i].expn = 0;
											j = k;
											new AePlayWave("merge.wav").start();
											tag = false;
											break;
										}
										else
										{
											break;
										}
									}
								}
							}
						}
					for(int i = 0;i < row;i++)
					{
						for(int j = 0;j < col;j++)
						{
							if(box[j][i].flag)
							{
								box[x][i].expn = box[j][i].expn;
								box[x][i].flag = true;
								box[x][i].button.setVisible(true);
								s += binary(box[x][i].expn);
								box[x][i].button.setText(s);
								box[x][i].button.setBackground(color[box[x][i].expn]);
								x++;
								s = "";
							}
							else
							{
								box[j][i].button.setVisible(false);
							}
						}
						for(int k = x;k < row;k++)
						{
							box[k][i].flag = false;
							box[k][i].expn = 0;
							box[k][i].button.setVisible(false);
						}
						x = 0;
					}
					s += count;
					J12.setText(s);
					s = "";
					//刷新最佳得分
					best_score = (best_score > count)? best_score:count;
					s += best_score;
					J14.setText(s);
					s = "";
					
					if(tag)
					{
						new AePlayWave("move.wav").start();
					}
					Restart();
				}
				else
				{
					Die();
				}
			}
			else if(key == KeyEvent.VK_DOWN)
			{
				x = col-1;
				if( movable("D") )//可以往右移动
				{
					for(int i = 0;i < row;i++)
						for(int j = col-1;j >=0 ;j--)
						{
							if( box[j][i].flag )
							{
								for(int k = j-1;k >= 0;k--)
								{
									if( box[k][i].flag )
									{
										if(box[j][i].expn == box[k][i].expn)
										{
											box[j][i].expn++;
											count += binary(box[j][i].expn);
											box[k][i].flag = false;
											box[k][i].expn = 0;
											j = k;
											new AePlayWave("merge.wav").start();
											tag = false;
											break;
										}
										else
										{
											break;
										}
									}
								}
							}
						}
					for(int i = 0;i < row;i++)
					{
						for(int j = col-1;j >= 0;j--)
						{
							if(box[j][i].flag)
							{
								box[x][i].expn = box[j][i].expn;
								box[x][i].flag = true;
								box[x][i].button.setVisible(true);
								s += binary(box[x][i].expn);
								box[x][i].button.setText(s);
								box[x][i].button.setBackground(color[box[x][i].expn]);
								x--;
								s = "";
							}
							else
							{
								box[j][i].button.setVisible(false);
							}
						}
						for(int k = x;k >= 0;k--)
						{
							box[k][i].flag = false;
							box[k][i].expn = 0;
							box[k][i].button.setVisible(false);
						}
						x = col-1;
					}
					s += count;
					J12.setText(s);
					s = "";
					//刷新最佳得分
					best_score = (best_score > count)? best_score:count;
					s += best_score;
					J14.setText(s);
					s = "";
					
					if(tag)
					{
						new AePlayWave("move.wav").start();
					}
					Restart();
				}
				else
				{
					Die();
				}
			}
			else{}
		}
	}
	
	//网络类
	class InternetMonitor extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			JLabel JLabel_temp = (JLabel)e.getSource();
			String J_temp = JLabel_temp.getText();
			System.out.println(J_temp);
			URI uri ;
				try {
					uri = new URI(J_temp);
					Desktop desk=Desktop.getDesktop();
					if(Desktop.isDesktopSupported() && desk.isSupported(Desktop.Action.BROWSE)){
						try {
							desk.browse(uri);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
		}
		public void mouseEntered(MouseEvent e){
			JLabel JLabel_temp = (JLabel)e.getSource();
			JLabel_temp.setForeground(Color.red);
		}
		public void mouseExited(MouseEvent e){
			JLabel JLabel_temp = (JLabel)e.getSource();
			JLabel_temp.setForeground(Color.blue);
		}
	}
	
	//计时器类
	class Timer extends Thread{  
	    public Timer(){
	        this.start();
	    }
	    @Override
	    public void run() {
	        while(true){
	            if(isRun){
	                sec +=1 ;
	                if(sec >= 60){
	                    sec = 0;
	                    min +=1 ;
	                }
	                if(min>=60){
	                    min=0;
	                    hour+=1;
	                }
	                showTime();
	            }
	 
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	             
	        }
	    }

	    private void showTime(){
	        String strTime ="" ;
	        if(hour < 10)
	            strTime = "0"+hour+":";
	        else
	            strTime = ""+hour+":";
	         
	        if(min < 10)
	            strTime = strTime+"0"+min+":";
	        else
	            strTime =strTime+ ""+min+":";
	         
	        if(sec < 10)
	            strTime = strTime+"0"+sec;
	        else
	            strTime = strTime+""+sec;
	         
	        //在窗体上设置显示时间
	        showtime.setText("   " + strTime);
	    }
	}
}

class AePlayWave extends Thread { 	 
    private String filename;
    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb 

    public AePlayWave(String wavfile) { 
        filename = wavfile;
    } 
    	    
    public void run() { 
        File soundFile = new File(filename); 
        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) { 
            e1.printStackTrace();
            return;
        } catch (IOException e1) { 
            e1.printStackTrace();
            return;
        } 
 
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) { 
            e.printStackTrace();
            return;
        } catch (Exception e) { 
            e.printStackTrace();
            return;
        } 

        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
        try { 
            while (nBytesRead != -1) { 
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                    auline.write(abData, 0, nBytesRead);
            } 
        } catch (IOException e) { 
            e.printStackTrace();
            return;
        } finally { 
            auline.drain();
            auline.close();
        } 
    } 
}
