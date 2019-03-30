package neuronal;

import com.jlmd.simpleneuralnetwork.neuralnetwork.NeuralNetwork;
import com.jlmd.simpleneuralnetwork.neuralnetwork.callback.INeuralNetworkCallback;
import com.jlmd.simpleneuralnetwork.neuralnetwork.entity.Error;
import com.jlmd.simpleneuralnetwork.neuralnetwork.entity.Result;

public class NN {

	public static void main(String[] args) {
		System.out.println("Starting neural network sample... ");

		float[][] x = { { 0.0f, 0.0f }, { 0.0f, 1.0f }, { 1.0f, 0.0f }, { 1.0f, 1.0f } };

		float[] t = { 1, 2, 3, 4 };

		NeuralNetwork neuralNetwork = new NeuralNetwork(x, t, new INeuralNetworkCallback() {
			@Override
			public void success(Result result) {
				float[] valueToPredict = new float[] { 0.9f, 0.9f };
				System.out.println("Success percentage: " + result.getSuccessPercentage());
				System.out.println("Predicted result: " + result.predictValue(valueToPredict));
			}

			@Override
			public void failure(Error error) {
				System.out.println("Error: " + error.getDescription());
			}
		});

		neuralNetwork.startLearning();
	}

}
