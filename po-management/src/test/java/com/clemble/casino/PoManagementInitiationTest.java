package com.clemble.casino;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.po.spring.web.management.PoManagementWebSpringConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = PoManagementWebSpringConfiguration.class)
public class PoManagementInitiationTest {

    @Test
    public void testInitialized() {
    }

}
