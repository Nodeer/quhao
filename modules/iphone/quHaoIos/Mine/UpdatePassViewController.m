//
//  UpdatePassViewController.m
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "UpdatePassViewController.h"

@interface UpdatePassViewController ()

@end

@implementation UpdatePassViewController
@synthesize aid;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = @"修改密码";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIScrollView  *scrollView =[[UIScrollView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    scrollView.backgroundColor = [UIColor whiteColor];
    self.view=scrollView;
    self.view.backgroundColor = [UIColor whiteColor];
    _currentField = [self createField:CGRectMake(15, 10.0f, kDeviceWidth-30, 31.0f) withName:@"当前密码"];
    [self.view addSubview:_currentField];
    _newField = [self createField:CGRectMake(15, 55.0f, kDeviceWidth-30, 31.0f) withName:@"新密码"];
    [self.view addSubview:_newField];
    _comfirmField = [self createField:CGRectMake(15, 100.0f, kDeviceWidth-30, 31.0f) withName:@"确认新密码"];
    [self.view addSubview:_comfirmField];
    [_currentField becomeFirstResponder];

    UIImage *btnImage = [UIImage   imageNamed:@"max_btn.png"];
    UIButton *tjBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    tjBtn.frame = CGRectMake(10, 140, 300, 30);
    [tjBtn setBackgroundImage:btnImage forState:UIControlStateNormal];
    [tjBtn setTitle: @"确认提交" forState: UIControlStateNormal];
    [tjBtn addTarget:self action:@selector(clickTj:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:tjBtn];
}

-(UITextField *)createField:(CGRect)size withName:(NSString *)title
{
    UITextField *currentField = [[UITextField alloc] initWithFrame:size];
    currentField.layer.borderColor = UIColor.grayColor.CGColor;
    currentField.layer.borderWidth = 1;
    currentField.layer.cornerRadius = 6.0;
    currentField.layer.masksToBounds = YES;
    currentField.clipsToBounds = YES;
    currentField.placeholder = title;
    currentField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    currentField.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    currentField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    currentField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    currentField.returnKeyType = UIReturnKeyDefault;  //键盘返回类型
    currentField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    currentField.keyboardType = UIKeyboardTypeASCIICapable;//键盘显示类型
    currentField.leftViewMode = UITextFieldViewModeAlways;
    currentField.backgroundColor=[UIColor whiteColor];
    //设置textField输入起始位置
    currentField.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 0)];
    currentField.leftViewMode = UITextFieldViewModeAlways;
    currentField.secureTextEntry = YES;
    
    return currentField;
}

- (void)clickTj:(id)sender
{
    if(_currentField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入当前密码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(_newField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入新密码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(_newField.text.length<6||_newField.text.length>16){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"新密码必须在6到16个字符" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if (![_newField.text isEqualToString:_comfirmField.text]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"两次输入的密码不一样" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    [self.view endEditing:YES];
    UITapGestureRecognizer *tap = (UITapGestureRecognizer *)sender;
    UIButton * btn = (UIButton *)tap;
    btn.enabled = NO;
    [_currentField resignFirstResponder];
    [_newField resignFirstResponder];
    [_comfirmField resignFirstResponder];
    if([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.removeFromSuperViewOnHide =YES;
        hud.labelText = NSLocalizedString(@"正在修改密码", nil);
        
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?accoutId=%@&newPassWord=%@&oldPass=%@",IP,updatePassword_url,self.aid,_newField.text,_currentField.text];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [hud hide:YES];
            btn.enabled = YES;
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"修改失败,请重试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
            return;
        }else{
            [hud hide:YES];
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                btn.enabled = YES;
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"修改失败,请重试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
                return;
            }else{
                NSString * errorKey=[jsonObjects valueForKey:@"errorKey"];
                NSString * errorText=[jsonObjects valueForKey:@"errorText"];
                btn.enabled = YES;
                if([errorKey isEqualToString:@"0"]){
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                    [alert show];
                    return;
                }else{
                    [Helper showHUD2:errorText andView:self.view andSize:100];
                    [self performSelector:@selector(clickToHome:) withObject:nil afterDelay:1.0f];
                }
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [_currentField resignFirstResponder];
    [_newField resignFirstResponder];
    [_comfirmField resignFirstResponder];
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
