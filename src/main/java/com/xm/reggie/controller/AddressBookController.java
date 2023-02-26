package com.xm.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xm.reggie.common.BaseContext;
import com.xm.reggie.common.R;
import com.xm.reggie.entity.AddressBook;
import com.xm.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 保存地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }


    /**
     * 设置默认地址，把当前用户的所有地址改为0，再把当前地址改为1
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();

        //把当前所有地址改为0
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(wrapper);

        //设置默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }


    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook == null){
            return R.error("地址不存在");
        }
        return R.success(addressBook);
    }


    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public R<AddressBook> getDefault(){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(wrapper);

        if(addressBook == null){
            return R.error("没有找到默认地址");
        }else {
            return R.success(addressBook);
        }
    }

    @GetMapping("/list")
    public R<List<AddressBook>> list(){

        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(wrapper));

    }

    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook){
        if(addressBook.getUserId().equals(BaseContext.getCurrentId())){
            addressBookService.updateById(addressBook);
            return R.success(addressBook);
        }else {
            return R.error("更新失败");
        }


    }

    @DeleteMapping
    public R<String> delete(Long ids){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getId,ids);
        AddressBook book = addressBookService.getOne(wrapper);
        if(book!=null){
            addressBookService.removeById(ids);
            return R.success("删除成功");
        }else {
            return R.error("删除失败");
        }


    }

}
