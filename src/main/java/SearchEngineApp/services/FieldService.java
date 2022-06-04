package SearchEngineApp.services;

import SearchEngineApp.dao.FieldDao;
import SearchEngineApp.models.Field;
import java.util.List;

public class FieldService
{
    private FieldDao fieldDao = new FieldDao();

    public FieldService() {}

    public void dropTable() {
        fieldDao.drop();
    }

    public void createTable() {
        fieldDao.create();
    }

    public void saveField(Field field) {
        fieldDao.save(field);
    }

    public List<Field> getFields() {
        return fieldDao.get();
    }

    public int getSize() {
        return fieldDao.getSize();
    }
}
