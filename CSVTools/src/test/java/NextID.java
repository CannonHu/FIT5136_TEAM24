import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class NextID {
    public static final String[] userType = {"admin", "coordinator", "candidate"};
    public static final String jsonName = "ids.json";

    public static final String adminKey = "adminNextId";
    public static final String coordinatorKey = "coordinatorNextId";
    public static final String candidateKey = "candidateNextId";

    private long adminNextId;
    private long candidateNextId;
    private long coordinatorNextId;
    private boolean changed = false;

    public static String basePath = null;

    public void flush() throws IOException {
        if (!changed)
            return;
        ObjectMapper mapper = new ObjectMapper();
        String oldFilePath = basePath + jsonName;
        String newFilePath = oldFilePath + '~';
        File newFile = new File(newFilePath);
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapper.writeValue(newFile, this);
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
            oldFile.delete();
        }

        newFile.renameTo(oldFile);
    }

    public long getAdminNextId() {
        return adminNextId;
    }

    public void setAdminNextId(long adminNextId) {
        if (adminNextId != this.adminNextId) {
            this.adminNextId = adminNextId;
            changed = true;
        }
    }

    public long getCandidateNextId() {
        return candidateNextId;
    }

    public void setCandidateNextId(long candidateNextId) {
        if (candidateNextId != this.candidateNextId) {
            this.candidateNextId = candidateNextId;
            changed = true;
        }
    }

    public long getCoordinatorNextId() {
        return coordinatorNextId;
    }

    public void setCoordinatorNextId(long coordinatorNextId) {
        if (coordinatorNextId != this.coordinatorNextId) {
            this.coordinatorNextId = coordinatorNextId;
            changed = true;
        }
    }

    public long useAdminId() {
        changed = true;
        return this.adminNextId++;
    }

    public long useCoordicatorId() {
        changed = true;
        return this.coordinatorNextId++;
    }

    public long useCandidateId() {
        changed = true;
        return this.candidateNextId++;
    }

    public long useNewId(String typeStr) {
        long newId = -1;
        if (typeStr.equals(userType[0])) {
            newId = useAdminId();
        } else if (typeStr.equals(userType[1])) {
            newId = useCoordicatorId();
        } else if (typeStr.equals(userType[2])) {
            newId = useCandidateId();
        }
        return newId;
    }

    public NextID (long aid, long coid, long canid) {
        this.adminNextId = aid;
        this.coordinatorNextId = coid;
        this.candidateNextId = canid;
    }
}
