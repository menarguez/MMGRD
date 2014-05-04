package datasetGenerator;


public class GenerateDatasetExample {

	private static CustomFunction2D fun1 = new CustomFunction2D() {
		@Override
		public double evaluate(double x, double y) {
			return x+y-2*x*y ;
		}
	};
	
	private static CustomFunction2D fun2 = new CustomFunction2D() {
		@Override
		public double evaluate(double x, double y) {
			return (x-1)+x*y*y-x*y+x*x*x;
		}
	};
	
	private static CustomFunction2D fun3 = new CustomFunction2D() {
		@Override
		public double evaluate(double x, double y) {
			return (x-1)+24*x*y*y-35*x*y+13*x*x*x;
		}
	};
	
	private static CustomFunction2D fun4 = new CustomFunction2D() {
		@Override
		public double evaluate(double x, double y) {
			return Math.pow(2, y)+16*x*x+6*x*y*y;
		}
	};
	
	private static CustomFunction2D fun5 = new CustomFunction2D() {
		@Override
		public double evaluate(double x, double y) {
			return Math.sin(x);
		}
	};
	public static void main(String[] args) {
		
		int num_samplesX = 10, num_samplesY = 10;
		
//		String filePathOneDTraining = "inputs\\2d\\x+y-2xy.csv";
//		CustomFunction2D selectedFunction = fun1;
		
//		String filePathOneDTraining = "inputs\\2d\\(x-1)+xyy-35xy+xxx.csv";
//		CustomFunction2D selectedFunction = fun2;
		
//		String filePathOneDTraining = "inputs\\2d\\(x-1)+24xyy-35xy+13xxx.csv";
//		CustomFunction2D selectedFunction = fun3;		
		
//		String filePathOneDTraining = "inputs\\2d\\2^(y)+16xx+6xyy.csv";
//		CustomFunction2D selectedFunction = fun4;		
		
		String filePathOneDTraining = "inputs\\2d\\sin(x).csv";
		CustomFunction2D selectedFunction = fun5;
		
		double[] inputFromRange = new double[] { -15.0d, -15d };
		double[] inputToRange = new double[] { 15.0d, 15.0d };
		
		int[] numSamplesTraining = new int[] { num_samplesX,
				num_samplesY };
		
		//Specify dimension and increment
		DatasetDimension dd = new DatasetDimension(inputFromRange,
				inputToRange, numSamplesTraining);
		//Generate file
		DatasetGenerator.generate2DCSV(filePathOneDTraining, dd,
				selectedFunction);
		System.out.println("Dataset created successfuly at "+filePathOneDTraining);

	}

}
