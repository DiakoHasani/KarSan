package ir.tdaapp.karsan.ViewModel;

//مربوط به فیلتر صفحه رزومه
public class VM_FilterCV {
    int madrakId, majorId, insuranceId, genderId;

    //مربوط به گواهینامه ها
    boolean base1, base2, base3, special, motorcycle;

    public VM_FilterCV() {
        madrakId = 0;
        majorId = 0;
        insuranceId = 0;
        genderId = 0;
        base1 = false;
        base2 = false;
        base3 = false;
        special = false;
        motorcycle = false;
    }

    public int getMadrakId() {
        return madrakId;
    }

    public void setMadrakId(int madrakId) {
        this.madrakId = madrakId;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public boolean isBase1() {
        return base1;
    }

    public void setBase1(boolean base1) {
        this.base1 = base1;
    }

    public boolean isBase2() {
        return base2;
    }

    public void setBase2(boolean base2) {
        this.base2 = base2;
    }

    public boolean isBase3() {
        return base3;
    }

    public void setBase3(boolean base3) {
        this.base3 = base3;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public boolean isMotorcycle() {
        return motorcycle;
    }

    public void setMotorcycle(boolean motorcycle) {
        this.motorcycle = motorcycle;
    }

    public boolean isEnableSearch() {
        boolean val = false;

        if (madrakId != 0) {
            val = true;
        }

        if (majorId != 0) {
            val = true;
        }

        if (insuranceId != 0) {
            val = true;
        }

        if (genderId != 0) {
            val = true;
        }

        if (base1) {
            val = true;
        }

        if (base2) {
            val = true;
        }

        if (base3) {
            val = true;
        }

        if (special) {
            val = true;
        }

        if (motorcycle) {
            val = true;
        }

        return val;
    }

}
