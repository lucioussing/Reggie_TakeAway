package com.it.reggie_takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie_takeaway.entity.AddressBook;
import com.it.reggie_takeaway.mapper.AddressBookMapper;
import com.it.reggie_takeaway.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
