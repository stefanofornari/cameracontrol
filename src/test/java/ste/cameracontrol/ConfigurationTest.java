/*
 * cameracontrol
 * Copyright (C) 2011 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */
package ste.cameracontrol;

import junit.framework.TestCase;

/**
 * Test class for the Configuration object
 * 
 * @author ste
 */
public class ConfigurationTest extends TestCase {

    private Configuration configuration;
    
    public ConfigurationTest(String testName) {
        super(testName);
        configuration = new Configuration();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSetAndGetImageDir() {
        final String TEST = "/tmp/cameracontrol";

        configuration.setImageDir(null);
        assertNull(configuration.getImageDir());

        configuration.setImageDir(TEST);
        assertEquals(TEST, configuration.getImageDir());
    }

}
