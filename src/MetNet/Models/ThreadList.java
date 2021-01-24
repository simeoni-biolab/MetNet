/*
    MetNet: comparison of Methabolic Networks
*/
package MetNet.models;

import java.util.ArrayList;

// thread manager
public class ThreadList {
    private static ArrayList<String> list = new ArrayList<>();
    
    //adding the thread id to the list of active threads
    protected static void addThread(String id){
        list.add(id);
    }

    /*
        The thread id is removed from the list of active threads.
        If the lists becomes empty all the threads ended up and the method returns true.
        The method is synchronized to avoid empty case mulfunction
    */
    protected static synchronized boolean notifyThread(String id){
        if(list.contains(id)){
            list.remove(list.indexOf(id));
        }
        return list.isEmpty();
    }    
}
