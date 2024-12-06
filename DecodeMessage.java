import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import java.lang.*;

public class DecodeMessage extends JFrame implements ActionListener
{
JButton open = new JButton("Load steganographed image"), decode = new JButton("Decode"),home =
new JButton("Back to main page");
JTextArea message = new JTextArea(10,3);
BufferedImage picture = null;
JScrollPane imagePane = new JScrollPane();

public DecodeMessage() {
super("Decode stegonographic message in image");
assembleInterface();
this.setDefaultCloseOperation(EXIT_ON_CLOSE);
this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
getMaximumWindowBounds());
this.setVisible(true);
this.validate();
}

private void assembleInterface() {
JPanel pon = new JPanel(new FlowLayout());
pon.add(open);
pon.add(decode);
pon.add(home);
this.getContentPane().add(pon, BorderLayout.NORTH);
open.addActionListener(this);
decode.addActionListener(this);
home.addActionListener(this);

pon = new JPanel(new GridLayout(1,1));
pon.add(new JScrollPane(message));
message.setFont(new Font("Times New Romain",Font.BOLD,20));

pon.setBorder(BorderFactory.createTitledBorder("Decoded message"));
message.setEditable(false);
this.getContentPane().add(pon, BorderLayout.SOUTH);

imagePane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
this.getContentPane().add(imagePane, BorderLayout.CENTER);

}

public void actionPerformed(ActionEvent ae) {
Object o = ae.getSource();
if(o == open)
openImage();
else if(o == decode)
decodeMessage();
else if(o == home)
homeInterface();
}

private java.io.File showFileDialog(boolean open) {
JFileChooser fc = new JFileChooser("Open an image");
javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
public boolean accept(java.io.File f) {
String name = f.getName().toLowerCase();
return f.isDirectory() || name.endsWith(".png") || name.endsWith(".bmp");
}
public String getDescription() {
return "Image (*.png, *.bmp)";
}
};
fc.setAcceptAllFileFilterUsed(false);
fc.addChoosableFileFilter(ff);

java.io.File f = null;
if(open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
f = fc.getSelectedFile();
else if(!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)

f = fc.getSelectedFile();
return f;
}

private void openImage() {
java.io.File f = showFileDialog(true);
try {
picture = ImageIO.read(f);
JLabel l = new JLabel(new ImageIcon(picture));
imagePane.getViewport().add(l);
this.validate();
} catch(Exception ex) { ex.printStackTrace(); }
}

private void decodeMessage() {
if(picture == null){
JOptionPane.showMessageDialog(this, "NO IMAGE TO DECODE MESSAGE",
"IMAGE NOT AVAILABLE", JOptionPane.ERROR_MESSAGE);
return;

}

String Str = JOptionPane.showInputDialog("Enter 4 digit pin");
int foo = Integer.parseInt(Str);
int pin= extractLength(picture, 0, 0);
int len = extractLength(picture, 32, 0);

if(foo==pin)
{

byte b[] = new byte[len];

for(int i=0; i<len; i++){
b[i] = extractByte(picture, i*8+64, 0);
message.setText(new String(b));

}
}
else{
JOptionPane.showMessageDialog(this, "pin not matched",
"ERROR", JOptionPane.ERROR_MESSAGE);

}

}

private byte extractByte(BufferedImage img, int start, int storageBit) {
int maxX = img.getWidth(), maxY = img.getHeight(),
startX = start/maxY, startY = start - startX*maxY, count=0;
byte b = 0;
for(int i=startX; i<maxX && count<8; i++) {
for(int j=startY; j<maxY && count<8; j++) {
int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
b = (byte)setBitValue(b, count, bit);
count++;
}
}

return b;
}
private int extractLength(BufferedImage img, int start, int storageBit) {
int maxX = img.getWidth(), maxY = img.getHeight(),
startX = start/maxY, startY = start - startX*maxY, count=0;
int length = 0;
for(int i=startX; i<maxX && count<32; i++) {
for(int j=startY; j<maxY && count<32; j++) {
int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
length = setBitValue(length, count, bit);
count++;
}
}
return length;
}

private void homeInterface(){
this.dispose();

Stegno h=new Stegno();
h.setSize(750,500);
h.setVisible(true);

}

private int getBitValue(int n, int location) {
int v = n & (int) Math.round(Math.pow(2, location));
return v==0?0:1;

}

private int setBitValue(int n, int location, int bit) {
int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
if(bv == bit)
return n;
if(bv == 0 && bit == 1)
n |= toggle;
else if(bv == 1 && bit == 0)
n ^= toggle;
return n;
}

public static void main(String arg[]) {
new DecodeMessage();
}
}