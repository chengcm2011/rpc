package com.ziroom.bsrd.rpc.server;


import com.ziroom.bsrd.client.IPersonService;
import com.ziroom.bsrd.client.Person;
import com.ziroom.bsrd.rpc.annotation.RpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luxiaoxun on 2016-03-10.
 */
@RpcService(IPersonService.class)
public class PersonServiceImpl implements IPersonService {

    @Override
    public List<Person> GetTestPerson(String name, int num) {
        List<Person> persons = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            persons.add(new Person(Integer.toString(i), name));
        }
        return persons;
    }
}
