/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportai_evolution;

/**
 *  SAI-E's Library for Neural Components
 *  (Stage 5) Neural OCR
 * 
 * @author Robert
 */
public class SAIE_NeuralBase {
    //Time to learn some neural, then find a library to do it better.
    class NeuralNetwork{
        class NeuralLayer{
            class Neuron{
                float wIn[];
                float bias;
                
                boolean updateW(float newW[]){return false;}
            }
            /* Neuron derivations go here. */
            
            
            Neuron layer[];
            NeuralLayer prev[],next[];
            
            boolean updateLayer(byte mode,NeuralLayer layer){
                //TODO: mode will indicate prev/next and add/remove/update layer
                
                return false;
            }
        }
        /*
        Since neural layers will be non-linear, in the fasion of not all paths are the same length,
            will have beginning and ending layers, input and output respective, marked.
        */
        NeuralLayer input[],output[];
        
        //This structure is great for building/customizing neurals, but hardly fast.
        //TODO: Build function to convert NN into optimized multi-threaded dotMatrix equations.
    }
}
