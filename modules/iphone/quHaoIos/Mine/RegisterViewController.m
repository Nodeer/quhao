//
//  RegisterController.m
//  quHaoIos
//
//  Created by sam on 13-10-27.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "RegisterViewController.h"

@interface RegisterViewController ()

@end

@implementation RegisterViewController
@synthesize accountField;
@synthesize mdField;
@synthesize passField;
@synthesize confirmPass;

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor=[UIColor whiteColor ];

    [self.scrollView setContentSize:CGSizeMake(kDeviceWidth, 480)];

    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToMine:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 注 册" rect:CGRectMake( 0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(addAccount:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

//按下Done按钮的调用方法，我们让键盘消失
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{    
    [self.view endEditing:YES];

    return YES;
}

//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

////输入框编辑完成以后，将视图恢复到原始状态
//-(void)textFieldDidEndEditing:(UITextField *)textField
//{
//    self.view.frame =CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
//}

-(void)addAccount:(id)sender
{
    if(self.accountField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.mdField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入验证码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.passField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入密码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.passField.text.length<6||self.passField.text.length>14){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"密码必须在6到14个字符" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if (![self.passField.text isEqualToString:self.confirmPass.text]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"两次输入的密码不一样" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    [self.view endEditing:YES];
    UITapGestureRecognizer *tap = (UITapGestureRecognizer *)sender;
    UIButton * btn = (UIButton *)tap;
    btn.enabled = NO;
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@%@&code=%@&password=%@",IP,register_url,self.accountField.text,self.mdField.text,self.passField.text];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSArray *jsonObjects=[QuHaoUtil analyseData:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                NSString * errorText=[jsonObjects valueForKey:@"errorText"];
                NSString * errorKey=[jsonObjects valueForKey:@"errorKey"];
                
                if(![errorKey isEqualToString:@"1"]){
                    [Helper showHUD2:errorText andView:self.view  andSize:100];

                }else{
                    [Helper showHUD2:errorText andView:self.view  andSize:100];
                    [self performSelector:@selector(clickToMine:) withObject:nil afterDelay:1.0f];
                }
            }
        }
    }else{
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.removeFromSuperViewOnHide =YES;
        //hud.mode = MBProgressHUDModeText;
        hud.labelText = NSLocalizedString(@"当前网络不可用", nil);
        hud.minSize = CGSizeMake(132.f, 108.0f);
        [hud hide:YES afterDelay:1];
    }
    btn.enabled = YES;
}

- (void)clickToMine:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (IBAction)hqCode:(id)sender {
    UITapGestureRecognizer *tap = (UITapGestureRecognizer *)sender;
    UIButton * btn = (UIButton *)tap;
    btn.enabled = NO;
    if(self.accountField.text.length!=0){
        if(self.accountField.text.length==11){
            NSString *urlStr=[NSString stringWithFormat:@"%@%@%@",IP,authCode_url,self.accountField.text];
            NSString *response =[QuHaoUtil requestDb:urlStr];
            if([response isEqualToString:@""]){
                //异常处理
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                NSArray *jsonObjects=[QuHaoUtil analyseData:response];
                if(jsonObjects==nil){
                    //解析错误
                    [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
                }else{
                    NSString * errorText=[jsonObjects valueForKey:@"errorText"];
                    
                    if(errorText.length!=0){
                        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                        [alert show];
                    }
                }
            }
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入正确的手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请先输入手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
    btn.enabled = YES;
}
@end

