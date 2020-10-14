package org.smartregister.unicefangola.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.unicefangola.BaseRobolectricTest;
import org.smartregister.unicefangola.application.UnicefAngolaApplication;
import org.smartregister.unicefangola.shadow.ShadowSQLiteDatabase;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 06-03-2020.
 */
@Config(shadows = {ShadowSQLiteDatabase.class})
public class UnicefAngolaRepositoryTest extends BaseRobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private UnicefAngolaRepository unicefAngolaRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        unicefAngolaRepository = Mockito.spy((UnicefAngolaRepository) UnicefAngolaApplication.getInstance().getRepository());

        Mockito.doReturn(sqLiteDatabase).when(unicefAngolaRepository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(unicefAngolaRepository).getReadableDatabase(Mockito.anyString());
        Mockito.doReturn(sqLiteDatabase).when(unicefAngolaRepository).getWritableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(unicefAngolaRepository).getWritableDatabase(Mockito.anyString());

        ReflectionHelpers.setField(UnicefAngolaApplication.getInstance(), "repository", unicefAngolaRepository);
    }

    // TODO: FIX THIS
    @Test
    public void onCreateShouldCreate32tables() {
        Mockito.doNothing().when(unicefAngolaRepository).onUpgrade(Mockito.any(SQLiteDatabase.class), Mockito.anyInt(), Mockito.anyInt());
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        unicefAngolaRepository.onCreate(database);

        // TODO: Investigate this counter
        Mockito.verify(database, Mockito.times(35)).execSQL(Mockito.contains("CREATE TABLE"));
    }
}