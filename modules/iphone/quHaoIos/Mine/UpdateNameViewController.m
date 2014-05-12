//
//  UpdateNameViewController.m
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "UpdateNameViewController.h"

@interface UpdateNameViewController ()

@end

@implementation UpdateNameViewController
@synthesize name;
@synthesize aid;
@synthesize delegate;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = @"修改用户名";
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
    
    UIButton *btnButton = [Helper getBtn:@" 确 定" rect:CGRectMake( 0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(clickXx:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
    
    self.view.backgroundColor = [UIColor whiteColor];
    _accountField = [[UITextField alloc] initWithFrame:CGRectMake(15, 10.0f, kDeviceWidth-30, 31.0f)];
    _accountField.layer.borderColor = UIColor.grayColor.CGColor;
    _accountField.layer.borderWidth = 1;
    _accountField.layer.cornerRadius = 6.0;
    _accountField.layer.masksToBounds = YES;
    _accountField.clipsToBounds = YES;
    _accountField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _accountField.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    _accountField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    _accountField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _accountField.returnKeyType = UIReturnKeyDefault;  //键盘返回类型
    _accountField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    _accountField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    _accountField.delegate = self;
    _accountField.leftViewMode = UITextFieldViewModeAlways;
    _accountField.backgroundColor=[UIColor whiteColor];
    //设置textField输入起始位置
    _accountField.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 0)];
    _accountField.leftViewMode = UITextFieldViewModeAlways;
    [self.view addSubview:_accountField];
    [_accountField becomeFirstResponder];
    
    UILabel *paddingLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 0, 70, 25)];
    paddingLabel.text = @" 用户名: ";
    paddingLabel.textColor = [UIColor blackColor];
    paddingLabel.backgroundColor = [UIColor clearColor];
    _accountField.leftView = paddingLabel;
    _accountField.text = self.name;
    
    UILabel *smLabel = [[UILabel alloc] initWithFrame:CGRectMake(25, 42, 290, 25)];
    smLabel.text = @"以英文字母或汉字开头,限4-16个字符,一个汉字为两个字符";
    smLabel.textColor = [UIColor darkGrayColor];
    smLabel.font = [UIFont systemFontOfSize:10];
    smLabel.backgroundColor = [UIColor clearColor];
    [self.view addSubview:smLabel];
}

//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [_accountField resignFirstResponder];
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (void)clickXx:(id)sender
{
    [_accountField resignFirstResponder];
    NSString * temp = _accountField.text;
    if ([[temp stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] isEqualToString:@""]){
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"用户名不能为空" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
            return;
    }
     NSString * tab = [temp substringToIndex:1];

    if(![self isChinese:[temp substringToIndex:1]] && ![self isAbc:tab]){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"用户名应以英文字母或汉字开头" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    NSInteger len = [Helper getStringLen:temp];
    if(len<4 || len>16){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"用户名长度应为4到16字符" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    if([Helper isConnectionAvailable]){
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.removeFromSuperViewOnHide =YES;
        hud.labelText = NSLocalizedString(@"正在修改用户名", nil);
        NSString *urlStr = [NSString stringWithFormat:@"%@%@?accoutId=%@&name=%@",IP,updateName_url,aid,[_accountField.text stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
        NSString *response = [QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [hud hide:YES];
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"修改失败,请重试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
            return;
        }else{
            [hud hide:YES];
            NSDictionary *obj=[QuHaoUtil analyseDataToDic:response];
 
            if([[obj objectForKey:@"errorKey"] isEqualToString:@"1"]){
                [delegate nameUpdate:temp];
                [self performSelector:@selector(clickToHome:) withObject:nil afterDelay:0.5f];
            }else if([[obj objectForKey:@"errorKey"] isEqualToString:@"2"]){
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"该用户名已被占用，另取一个用户名吧" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
                return;
            }else{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"修改失败,请重试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
                return;
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (BOOL)isAbc:(NSString *) str
{
    char ch = [str characterAtIndex:0];
    if ((((ch)>='A'&&ch<='Z')||((ch)>='a'&&(ch)<='z'))) {
        return true;
    }else{
        return false;
    }
}

- (BOOL)isChinese:(NSString*)c{
    int strlength = 0;
    char* p = (char*)[c cStringUsingEncoding:NSUnicodeStringEncoding];
    for (int i=0 ; i<[c lengthOfBytesUsingEncoding:NSUnicodeStringEncoding] ;i++) {
        if (*p) {
            p++;
            strlength++;
        }
        else {
            p++;
        }
    }
    return ((strlength/2)==1)?YES:NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
