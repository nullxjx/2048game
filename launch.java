import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import java.io.*;

public class launch {
	public static void main(String args[]){
		new MainWindow();
	}
}

class Box{
    JButton button;
	int expn;
	boolean flag;
}

class MainWindow extends JFrame {
	private static Box[][] box = new Box[4][4];
	private Color[] color = new Color[12];//2048 每种数值的方块一种颜色
	private Point point;//记录父窗体在屏幕的坐标
	private int width,height;//父窗体的宽度和高度
	JTextField J12,J14;
	JLabel showtime;
	JDialog dialog2;
	JLabel b;
	String [] image = new String[3];
	int tag = 0;
	int count = 0;//记录当前得分,初始得分为0
	int best_score = 0;//记录最佳得分，初始化为0，每次开始时重txt中读取数据刷新该值
	String temp = "";
	//定义变量存储时分秒
    int hour =0;
    int min =0;
    int sec =0 ;
    boolean isRun = true;
    
	public MainWindow(){
		super("2048 By XJX");
		setSize(500,600);
		setFocusable(true);
		setLayout(new BorderLayout());
		setResizable(false);
		JPanel J1 = new JPanel();
		J1.setBackground(Color.gray);
		JPanel J2 = new JPanel();
		J1.setLayout(new GridLayout(1,4,2,2));
		J2.setLayout(new GridLayout(4,4,10,10));
		J2.setFocusable(true);
		JPanel J3 = new JPanel();
		J3.setLayout(new GridLayout(1,2,2,2));
		JButton J31 = new JButton("RESTART");
		JButton J32 = new JButton("UNDO");
		J31.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int result=JOptionPane.showConfirmDialog(null, "Are you sure to restart the game?", "Information", JOptionPane.YES_NO_OPTION);
					if(result==JOptionPane.YES_NO_OPTION)
					{
						for(int i = 0;i < 4;i++)
							for(int j = 0;j < 4;j++)
							{
								box[i][j].flag = false;
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
						J31.setFocusable(false);
						J32.setFocusable(false);
						setFocusable(true);
					}
				}
			}
		);
		J3.add(J31);
		J3.add(J32);
		JPanel J4 = new JPanel();
		J4.setLayout(new GridLayout(2,1,2,2));
		J4.add(J1);
		JPanel timePanel = new JPanel();
		timePanel.setBackground(Color.magenta);
		timePanel.setLayout(new GridLayout(1,2,2,2));
		JLabel timeLabel = new JLabel("            Time used:");
		showtime = new JLabel("");
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
		J11.setFont(new Font("宋体",Font.BOLD,20));
		J12.setFont(new Font("宋体",Font.BOLD,20));
		J13.setFont(new Font("宋体",Font.BOLD,20));
		J14.setFont(new Font("宋体",Font.BOLD,20));
		J31.setFont(new Font("宋体",Font.BOLD,20));
		J32.setFont(new Font("宋体",Font.BOLD,20));
		timeLabel.setFont(new Font("宋体",Font.BOLD,20));
		showtime.setFont(new Font("宋体",Font.BOLD,20));
		J1.add(J11);
		J1.add(J12);
		J1.add(J13);
		J1.add(J14);
		add(J4,BorderLayout.NORTH);
		add(J2,BorderLayout.CENTER);
		add(J3,BorderLayout.SOUTH);
		for(int i = 0;i < 4;i++)
			for(int j = 0; j < 4;j++)
			{
				box[i][j] = new Box();
				box[i][j].expn = 0;//指数初始化为0
				box[i][j].flag = false;//初始化为不可见
				box[i][j].button = new JButton();
				box[i][j].button.setFont(new Font("宋体",Font.BOLD,40));
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
		//弹出框部分
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		JMenu fileMenu = new JMenu("File");
		bar.add(fileMenu);
		JMenuItem aboutItem = new JMenuItem("About...");
		JMenuItem helpItem = new JMenuItem("Help");
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(aboutItem);
		fileMenu.add(helpItem);
		fileMenu.add(exitItem);
		JDialog dialog = new JDialog();
		dialog.setTitle("About");
		dialog.setSize(450, 300);
		dialog.setLayout(new GridLayout(2,1));
		dialog2 = new JDialog();
		dialog2.setTitle("How to play");
		dialog2.setSize(400, 400);
		dialog2.addKeyListener(
				new  KeyAdapter(){
					public void keyPressed(KeyEvent e){
						int key = e.getKeyCode();
						if(key == KeyEvent.VK_RIGHT)
						{
							tag++;
							if(tag > 2)
								tag = 0;
							b.setIcon(new ImageIcon(getClass().getResource(image[tag])));
						}
					}
				}
		);
		
		b = new JLabel();
		image[0] = "bug0.png";
		image[1] = "bug1.png";
		image[2] = "bug2.png";
		Icon bug = new ImageIcon(getClass().getResource(image[0]));
		b = new JLabel("点击   >  键继续",bug,SwingConstants.CENTER);
		b.setHorizontalTextPosition(SwingConstants.CENTER);
		b.setVerticalTextPosition(SwingConstants.BOTTOM);
		
		JTextArea J6 = new JTextArea("版本：2017_05_11_1.0.0\nWritten By XJX \nMy Email: thexjx@gmail.com\n\n欢迎访问我的主页:");
		J6.setFont(new Font("宋体",Font.BOLD,20));
		J6.setEditable(false);
		dialog.add(J6);
		JPanel J7 = new JPanel();
		J7.setLayout(new GridLayout(4,1));
		J7.setBackground(Color.white);
		dialog.add(J7);
		JLabel MyGithub_Label = new JLabel("Github:");
		MyGithub_Label.setFont(new Font("宋体",Font.BOLD,15));
		final JLabel MyGithub = new JLabel("https://github.com/JiaxinTse");
		MyGithub.setFont(new Font("宋体",Font.BOLD,15));
		MyGithub.setBackground(Color.white);
		MyGithub.addMouseListener(new InternetMonitor());
		JLabel MyCnBlog_Label = new JLabel("博客园:");
		MyCnBlog_Label.setFont(new Font("宋体",Font.BOLD,15));
		final JLabel MyCnBlog = new JLabel("http://www.cnblogs.com/journal-of-xjx/");
		MyCnBlog.setFont(new Font("宋体",Font.BOLD,15));
		MyCnBlog.addMouseListener(new InternetMonitor());
		J7.add(MyGithub_Label);
		J7.add(MyGithub);
		J7.add(MyCnBlog_Label);
		J7.add(MyCnBlog);
		aboutItem.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent e){
					point = MainWindow.this.getLocation();//获得主窗体在屏幕的坐标
					width = MainWindow.this.getWidth();
					height = MainWindow.this.getHeight();
					dialog.setLocation(
					        point.x + width/2 - dialog.getWidth()/2, 
					        point.y + height/2 - dialog.getHeight()/2);
					dialog.setVisible(true);
				}
			}
		);
		helpItem.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						point = MainWindow.this.getLocation();//获得主窗体在屏幕的坐标
						width = MainWindow.this.getWidth();
						height = MainWindow.this.getHeight();
						dialog2.setLocation(
						        point.x + width/2 - dialog2.getWidth()/2, 
						        point.y + height/2 - dialog2.getHeight()/2);
						tag = 0;
						dialog2.add(b,BorderLayout.CENTER);
						b.setIcon(new ImageIcon(getClass().getResource(image[tag])));
						dialog2.setVisible(true);
					}
				}
		);
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
					 //每次退出程序之前保存最佳得分，方便下一次使用
					try {
						if(J14.getText().equals(""))//如果退出的时候BEST显示为空，说明当前best为0
						{
							temp += count;
							write_best(temp);
							temp = "";
						}
						else
						{
							temp = J14.getText();
							best_score = StringToInt(temp);
							best_score = (best_score > count)? best_score:count;
							temp = "";
							temp += best_score;
							write_best(temp);
							temp = "";
						}
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
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
		
		//程序开始
		
		new Timer();
		//程序开始运行时随机产生2个方块
		produceRandom();
		produceRandom();
		//开始时读取txt中的内容，设为best的初始值
		try {
			temp = read_best();
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		J14.setText(temp);
		temp = "";
	}
	
	String read_best() throws IOException {
		String best = "0";
		FileInputStream fis = new FileInputStream("D:\\MyEclipse 2016 CI\\2048game\\src\\best_score.txt");
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
	
	void write_best(String best) throws IOException {
		FileOutputStream fos = new FileOutputStream("D:\\MyEclipse 2016 CI\\2048game\\src\\best_score.txt");
		DataOutputStream dos = new DataOutputStream(fos);
		try
		{
			dos.writeBytes(best);
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
		for(int i = 0;i < 4;i++)
			for(int j = 0;j < 4;j++)
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
		int x = rand.nextInt(4);
		int y = rand.nextInt(4);
		int z = rand.nextInt(2)+1;//z为1或者2
		while(box[x][y].flag){
			x = rand.nextInt(4);
			y = rand.nextInt(4);
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
		for(int i = 0;i < 4;i++)
			for(int j = 0;j < 4;j++)
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
			for(int i = 0;i < 4;i++)
			{
				for(int j = 3;j >= 1;j--)//每一行从右往左找
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
			for(int i = 0;i < 4;i++)
			{
				for(int j = 0;j < 3;j++)//每一行从左往右找
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
			for(int i = 0;i < 4;i++)
			{
				for(int j = 3;j >= 1;j--)//每一列从下往上找
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
			for(int i = 0;i < 4;i++)
			{
				for(int j = 0;j < 3;j++)//每一列从上往下找
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
	//实现移动功能的类	
	class KeyMonitor extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			int key = e.getKeyCode();
			int x = 0;
			String s = "";
			if(key == KeyEvent.VK_LEFT)
			{
				x = 0;
				if( movable("L") )//可以往左移动
				{
					for(int i = 0;i < 4;i++)
						for(int j = 0;j < 4;j++)
						{
							if( box[i][j].flag )
							{
								for(int k = j+1;k < 4;k++)
								{
									if( box[i][k].flag )
									{
										if(box[i][j].expn == box[i][k].expn)
										{
											box[i][j].expn++;
											count += binary(box[i][j].expn);
											box[i][k].flag = false;
											j = k;
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
					
					for(int i = 0;i < 4;i++)
					{
						for(int j = 0;j < 4;j++)
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
						for(int k = x; k < 4;k++)
						{
							box[i][k].flag = false;
							box[i][k].button.setVisible(false);
						}
						x = 0;
					}
					//刷新当前得分
					s += count;
					J12.setText(s);
					s = "";
					
					if(success())//每次移动完都判断是否赢了
					{
						int result=JOptionPane.showConfirmDialog(null, "恭喜你赢了！是否继续？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
					else
					{
						produceRandom();
					}
				}
				else
				{
					if(isfull() && !movable("R") && !movable("U") && !movable("D"))//当前的布局已满而且各个方向都不可移动
					{
						int result=JOptionPane.showConfirmDialog(null, "对不起，你输了！是否重来一局？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
				}
			}
			else if(key == KeyEvent.VK_RIGHT)
			{
				x = 3;
				if( movable("R") )//可以往右移动
				{
					for(int i = 0;i < 4;i++)
						for(int j = 3;j >=0 ;j--)
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
											j = k;
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
					for(int i = 0;i < 4;i++)
					{
						for(int j = 3;j >= 0;j--)
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
							box[i][k].button.setVisible(false);
						}
						x = 3;
					}
					s += count;
					J12.setText(s);
					s = "";
				
					if(success())//每次移动完都判断是否赢了
					{
						int result=JOptionPane.showConfirmDialog(null, "恭喜你赢了！是否继续？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
					else
					{
						produceRandom();
					}
				}
				else
				{
					if(isfull() && !movable("L") && !movable("U") && !movable("D"))//当前的布局已满而且各个方向都不可移动
					{
						int result=JOptionPane.showConfirmDialog(null, "对不起，你输了！是否重来一局？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
				}
			}
			else if(key == KeyEvent.VK_UP)
			{
				x = 0;
				if( movable("U") )//可以往上移动
				{
					for(int i = 0;i < 4;i++)
						for(int j = 0;j < 4;j++)
						{
							if( box[j][i].flag )
							{
								for(int k = j+1;k < 4;k++)
								{
									if( box[k][i].flag )
									{
										if(box[j][i].expn == box[k][i].expn)
										{
											box[j][i].expn++;
											count += binary(box[j][i].expn);
											box[k][i].flag = false;
											j = k;
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
					for(int i = 0;i < 4;i++)
					{
						for(int j = 0;j < 4;j++)
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
						for(int k = x;k < 4;k++)
						{
							box[k][i].flag = false;
							box[k][i].button.setVisible(false);
						}
						x = 0;
					}
					s += count;
					J12.setText(s);
					s = "";

					if(success())//每次移动完都判断是否赢了
					{
						int result=JOptionPane.showConfirmDialog(null, "恭喜你赢了！是否继续？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
					else
					{
						produceRandom();
					}
				}
				else
				{
					if(isfull() && !movable("L") && !movable("R") && !movable("D"))//当前的布局已满而且各个方向都不可移动
					{
						int result=JOptionPane.showConfirmDialog(null, "对不起，你输了！是否重来一局？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
				}
			}
			else if(key == KeyEvent.VK_DOWN)
			{
				x = 3;
				if( movable("D") )//可以往右移动
				{
					for(int i = 0;i < 4;i++)
						for(int j = 3;j >=0 ;j--)
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
											j = k;
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
					for(int i = 0;i < 4;i++)
					{
						for(int j = 3;j >= 0;j--)
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
							box[k][i].button.setVisible(false);
						}
						x = 3;
					}
					s += count;
					J12.setText(s);
					s = "";
					
					if(success())//每次移动完都判断是否赢了
					{
						int result=JOptionPane.showConfirmDialog(null, "恭喜你赢了！是否继续？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
					else
					{
						produceRandom();
					}
				}
				else
				{
					if(isfull() && !movable("L") && !movable("R") && !movable("U"))//当前的布局已满而且各个方向都不可移动
					{
						int result=JOptionPane.showConfirmDialog(null, "对不起，你输了！是否重来一局？", "Information", JOptionPane.YES_NO_OPTION);
						if(result==JOptionPane.YES_NO_OPTION)
						{
							for(int i = 0;i < 4;i++)
								for(int j = 0;j < 4;j++)
								{
									box[i][j].flag = false;
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
						}
					}
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
							// TODO 自动生成的 catch 块
							e1.printStackTrace();
						}
					}
				} catch (URISyntaxException e1) {
					// TODO 自动生成的 catch 块
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
	public class Timer extends Thread{  
	    public Timer(){
	        this.start();
	    }
	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
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
	                // TODO Auto-generated catch block
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
	        showtime.setText(strTime);
	    }
	}
}