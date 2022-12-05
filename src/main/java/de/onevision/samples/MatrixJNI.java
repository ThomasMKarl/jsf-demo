package de.onevision.samples;

public class MatrixJNI {

    static {
        System.load("D:/jsf-demo/src/main/java/de/onevision/samples/hello.dll");
    }
    
    public static void main(String[] args) {
        MatrixJNI help = new MatrixJNI();
	help.sayHello();

	String[] toCat = {"bli", "bla", "blub"};
	System.out.println(help.cat(toCat, " "));
    }

    private native String cat(String[] toCat, String delim);

    // Declare a native method sayHello() that receives no arguments and returns void
    private native void sayHello();
}
