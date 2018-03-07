package com.ziroom.bsrd.rpc.test;

import java.util.List;


public interface IPersonService {
    List<Person> GetTestPerson(String name, int num);
}
