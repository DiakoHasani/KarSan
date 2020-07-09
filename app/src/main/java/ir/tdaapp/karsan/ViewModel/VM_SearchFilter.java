package ir.tdaapp.karsan.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ir.tdaapp.karsan.Enum.InsuranceHistory;
import ir.tdaapp.karsan.Enum.JobHistory;
import ir.tdaapp.karsan.Enum.Job_Time;

//در اینجا فیلترهای جستجو ست می شوند
public class VM_SearchFilter {

    //در اینجا معلوم می شود که آیا بر اساس این فیلترها جستجو شود
    private boolean Status = false;

    //برای حداقل قیمت
    private String MinRights = "";

    //حداکثر قیمت
    private String MaxRights = "";

    //تایم کار
    private List<Job_Time> job_time = new ArrayList<>();

    //سابقه بیمه
    private InsuranceHistory insuranceHistory = InsuranceHistory.Ignored;

    //سابقه کار
    private JobHistory jobHistory = JobHistory.Ignored;

    //مدرک
    private int Madrak = 0;

    //رشته تحصیلی
    private int Major = 0;

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getMinRights() {
        return MinRights;
    }

    public void setMinRights(String minRights) {
        MinRights = minRights;
    }

    public String getMaxRights() {
        return MaxRights;
    }

    public void setMaxRights(String maxRights) {
        MaxRights = maxRights;
    }

    public InsuranceHistory getInsuranceHistory() {
        return insuranceHistory;
    }

    public void setInsuranceHistory(InsuranceHistory insuranceHistory) {
        this.insuranceHistory = insuranceHistory;
    }

    public JobHistory getJobHistory() {
        return jobHistory;
    }

    public void setJobHistory(JobHistory jobHistory) {
        this.jobHistory = jobHistory;
    }

    public int getMadrak() {
        return Madrak;
    }

    public void setMadrak(int madrak) {
        Madrak = madrak;
    }

    public int getMajor() {
        return Major;
    }

    public void setMajor(int major) {
        Major = major;
    }

    public List<Job_Time> getJob_time() {
        return job_time;
    }

    public void setJob_time(List<Job_Time> job_time) {
        this.job_time = job_time;
    }

    //در اینجا چک می شود که کاربر از جستجو استفاده کرده است یا خیر
    public boolean IsEnabledSearched() {
        boolean val = false;

        if (isStatus()) {
            val = true;
        }

        if (!getMinRights().equalsIgnoreCase("")) {
            val = true;
        }

        if (!getMaxRights().equalsIgnoreCase("")) {
            val = true;
        }

        if (getJob_time().size() > 0) {
            val = true;
        }

        if (getInsuranceHistory() != InsuranceHistory.Ignored) {
            val = true;
        }

        if (getJobHistory() != JobHistory.Ignored) {
            val = true;
        }

        if (getMadrak() != 0) {
            val = true;
        }

        if (getMajor() != 0) {
            val = true;
        }

        return val;
    }
}
