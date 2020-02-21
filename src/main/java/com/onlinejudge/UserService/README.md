# UserService 用户信息服务

## UserServiceCheckToken
功能 :

    检查用户Token是否是最新的(为了确保同一时间操作的只有一个用户)

参数 : 

    String userToken //用户的Token
    
## UserServiceUpdatePassword

功能: 
    
    修改密码
    
参数: 
    
    String userID, alterPassword;

注意: 

    这个修改并不检查其他的信息, 用户权限验证可以用Token实现, 比如
    重新刷新一下token

## UserServiceUpdateUserName

功能: 
    
    更新用户昵称
    
参数: 
    
    String userID, alterUsername;

注意: 

    更新的是昵称而不是用户名(userID), 也不是Token(uuid)

## UserServiceDeleteAccount

TODO: 级联删除还有bug


功能: 

    删除账户
   
参数: 

    String userID
    
注意:

    删除了就没有了, 数据无价! 
     
## UserServiceAddUser

功能 :
    
    添加一个新的用户
    
参数 :

    String userID, userPassword, userName