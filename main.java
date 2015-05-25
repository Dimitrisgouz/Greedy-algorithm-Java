import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Job {
  
  private final int downloadTime;
  private final int workTime;

  public Job(int downloadTime, int workTime) {
    this.downloadTime = downloadTime;
    this.workTime = workTime;
  }

  public int getWorkTime() {
    return this.workTime;
  }

  public int getDownloadTime() {
    return this.downloadTime;
  }

  @Override
  public String toString() {
    return "Job: downloadTime: " + Integer.toString(this.downloadTime) + 
            " workTime: " + Integer.toString(this.workTime) + "\n";
  }

  public int morePrimary(Job job) {
      return job.workTime - this.workTime;
  }
}

class Processor {

  private final int DEFAULT_PROCESSOR_START_TIME = 0;

  private int timeOfWork; 

  public Processor() {
    this.timeOfWork = DEFAULT_PROCESSOR_START_TIME;
  }

  public int getTimeOfWork() {
    return this.timeOfWork;
  }

  public void incrementTimeOfWork(int value) {
    this.timeOfWork += value;
  }
  
  public void setTimeOfWork(int value) {
    this.timeOfWork = value;
  }
}

class ProcessorTimeManager {

  private final int INIT_TIME_OF_COMPUTER_WORK = 0;

  private int timeOfComputerWork;
  private final List<Processor> proccessors;
  private final List<Job> jobs;
  private final Processor serverProcessor;

  public ProcessorTimeManager(List<Processor> proccessors, List<Job> jobs) {
    this.jobs = jobs;
    this.proccessors = proccessors;
    this.timeOfComputerWork = INIT_TIME_OF_COMPUTER_WORK;

    this.serverProcessor = new Processor();
  }

  // method which manages all jobs
  public void executeJobs() {
    this.printJobs();
    this.sortProcessorsByFreeTime();
    this.sortJobsByPriority();
    this.printJobs();

    // go over all jobs
    // sort all processors and get most useless
    // set to this process current job
    for (Job job : jobs) {
      this.proccessors.get(0).incrementTimeOfWork(job.getWorkTime() + job.getDownloadTime());
      this.serverProcessor.incrementTimeOfWork(job.getDownloadTime());
      this.timeOfComputerWork += job.getDownloadTime();
      this.sortProcessorsByFreeTime();

      // set serverProcess to all processors which are in pause
      for (Processor proccessor : this.proccessors) {
          if (proccessor.getTimeOfWork() < this.serverProcessor.getTimeOfWork()) {
            proccessor.setTimeOfWork(this.serverProcessor.getTimeOfWork()); 
          }
      }
    
      // if most useless processor time is bigger than server than server should be in pause
      if(this.proccessors.get(0).getTimeOfWork() > this.serverProcessor.getTimeOfWork()) {
        this.serverProcessor.setTimeOfWork(this.proccessors.get(0).getTimeOfWork());
        this.timeOfComputerWork = this.proccessors.get(0).getTimeOfWork();
      }
    }

    this.sortProcessorsByFreeTime();

    // result is work time of biggest processor
    this.timeOfComputerWork = this.proccessors.get(this.proccessors.size()-1).getTimeOfWork();
  }

  public int getWorkTime() {
    return this.timeOfComputerWork;
  }
  
  protected void sortProcessorsByFreeTime() {
    Collections.sort(this.proccessors, new Comparator<Processor>() {
      @Override
      public int compare(Processor a, Processor b) {
        return a.getTimeOfWork() > b.getTimeOfWork() ? 1 : -1;
      }
    });
  }
 
  protected void sortJobsByPriority() {
    Collections.sort(this.jobs, new Comparator<Job>() {
      @Override
      public int compare(Job a, Job b) {
        return a.morePrimary(b);
      }
    });
  }

  private void printJobs() {
    for(Job job : this.jobs) {
       System.out.println(job);
    }
    System.out.println("\n");
  }

}

class main {
  public static void main(String[] args) throws FileNotFoundException, IOException {
        
    BufferedReader br = new BufferedReader(new FileReader("input.txt"));
    
    int processorsAmount = 0;
    int jobsAmount = 0;
    List<Job> jobs = new ArrayList<Job>();
    List<Processor> processors = new ArrayList<Processor>();
    
    try {
      StringBuilder sb = new StringBuilder();

      processorsAmount = Integer.parseInt(br.readLine());
      for(int index = 0; index < processorsAmount; index++) {
          processors.add(new Processor());
      }
      
      jobsAmount = Integer.parseInt(br.readLine());
      
      for(int index = 0; index < jobsAmount; index++) {
        String[] parts = br.readLine().split(" ");
        jobs.add(new Job(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
      }
      
    } finally {
      br.close();
    }
    
    ProcessorTimeManager manager = new ProcessorTimeManager(processors, jobs);
    manager.executeJobs();
    
    PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
    writer.print(Integer.toString(manager.getWorkTime()));
    writer.close();
  }
}