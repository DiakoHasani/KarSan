package ir.tdaapp.karsan.ViewModel;

public class VM_Versions {
    private float VersionSql,VersionApp;

    public VM_Versions() { }
    public VM_Versions(float versionSql, float versionApp) {
        VersionSql = versionSql;
        VersionApp = versionApp;
    }

    public float getVersionSql() {
        return VersionSql;
    }

    public float getVersionApp() {
        return VersionApp;
    }

    public void setVersionSql(float versionSql) {
        VersionSql = versionSql;
    }

    public void setVersionApp(float versionApp) {
        VersionApp = versionApp;
    }
}
