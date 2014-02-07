//
//  AppraiseViewController.m
//  quHaoIos
//
//  Created by sam on 13-11-13.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "AppraiseViewController.h"

@interface AppraiseViewController ()

@end

@implementation AppraiseViewController
@synthesize customNumberOfStars;
@synthesize fwNumberOfStars;
@synthesize hjNumberOfStars;
@synthesize xjbNumberOfStars;
@synthesize ztNumberOfStars;
@synthesize merchartID;
@synthesize accouId;
@synthesize rid;
@synthesize textView;
@synthesize isCommented;
@synthesize accountField;

-(void)loadView
{
    UIView  *view=[[UIView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    self.view=view;
    
    [self loadNavigationItem];
}

-(void)loadNavigationItem
{
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 提 交" rect:CGRectMake( 0, 7, 50, 30 )];
    [btnButton addTarget:self action:@selector(clickXx:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor=[UIColor whiteColor];
    UILabel *pl = [Helper getLabel:@"人均消费" font:18 rect:CGRectMake(0, 8, 85, 20)];
    [self.view addSubview:pl];
    
    self.accountField = [[UITextField alloc] initWithFrame:CGRectMake(80, 5, 100.0f, 27.0f)];
    [accountField setBorderStyle:UITextBorderStyleRoundedRect]; //外框类型
    accountField.placeholder = @""; //默认显示的字
    accountField.secureTextEntry = NO; //是否以密码形式显示
    accountField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    accountField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    accountField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    accountField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    accountField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;//键盘显示类型
    accountField.delegate = self;
    [self.view addSubview:accountField];

    UILabel *name = [Helper getLabel:@"口味" font:18 rect:CGRectMake(0, 30, 70, 70)];
    [self.view addSubview:name];
	customNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(40, 40, 300, 70) andStars:5 isFractional:YES];
    [self setRating:customNumberOfStars];
    
    UILabel *hj = [Helper getLabel:@"环境" font:18 rect:CGRectMake(0, 85, 70, 70)];
    [self.view addSubview:hj];
	hjNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(40, 100, 300, 70) andStars:5 isFractional:YES];
    [self setRating:hjNumberOfStars];

    UILabel *fw = [Helper getLabel:@"服务" font:18 rect:CGRectMake(0, 140, 70, 70)];
    [self.view addSubview:fw];
    fwNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(40, 160, 300, 70) andStars:5 isFractional:YES];
    [self setRating:fwNumberOfStars];

    UILabel *xjb = [Helper getLabel:@"性价比" font:18 rect:CGRectMake(0, 195, 65, 70)];
    [self.view addSubview:xjb];
	xjbNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(40, 220, 300, 70) andStars:5 isFractional:YES];
    [self setRating:xjbNumberOfStars];

    UILabel *zt = [Helper getLabel:@"总体评价" font:18 rect:CGRectMake(0, 250, 90, 70)];
    [self.view addSubview:zt];
    ztNumberOfStars = [[RatingControl alloc] initWithFrame:CGRectMake(40, 280, 300, 70) andStars:5 isFractional:YES];
    [self setRating:ztNumberOfStars];
    
    self.textView = [[UITextView alloc] initWithFrame:CGRectMake(8, 315, 290, 110)];
    self.textView.textColor = [UIColor grayColor];//设置textview里面的字体颜色
    self.textView.layer.borderColor = UIColor.grayColor.CGColor;
    self.textView.layer.borderWidth = 1;
    self.textView.layer.cornerRadius = 6.0;
    self.textView.layer.masksToBounds = YES;
    self.textView.clipsToBounds = YES;
    self.textView.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    self.textView.delegate = self;//设置它的委托方法
    self.textView.backgroundColor = [UIColor clearColor];//设置它的背景颜色
    self.textView.text = @"我要评论";//设置它显示的内容
    self.textView.returnKeyType = UIReturnKeyDefault;//返回键的类型
    self.textView.keyboardType = UIKeyboardTypeDefault;//键盘类型
    self.textView.scrollEnabled = YES;//是否可以拖动
    self.textView.autoresizingMask = UIViewAutoresizingFlexibleHeight;//自适应高度
    [self.view addSubview: self.textView];
    
    if (self.isCommented) {
        [self getComment];
    }
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

-(void)setRating:(RatingControl*)control
{
    control.delegate = self;
	control.backgroundColor = [UIColor groupTableViewBackgroundColor];
	control.autoresizingMask =  UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin;
	control.rating = 0;
	[self.view addSubview:control];
}

-(void)getComment
{
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?rid=%@",[Helper getIp],getLatestComment_url,rid];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            NSDictionary *jsonObjects=[QuHaoUtil analyseDataToDic:response];
            if(jsonObjects==nil){
                //解析错误
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }else{
                if(jsonObjects.count!=0){
                    self.customNumberOfStars.rating=[[jsonObjects objectForKey:@"kouwei"] floatValue];
                    self.hjNumberOfStars.rating=[[jsonObjects  objectForKey:@"huanjing"] floatValue];
                    self.fwNumberOfStars.rating=[[jsonObjects objectForKey:@"fuwu"] floatValue];
                    self.xjbNumberOfStars.rating=[[jsonObjects  objectForKey:@"xingjiabi"] floatValue];
                    self.ztNumberOfStars.rating=[[jsonObjects  objectForKey:@"grade"] floatValue];
                    self.textView.text=[jsonObjects objectForKey:@"content"];
                    self.accountField.text=[jsonObjects  objectForKey:@"averageCost"];
                }
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

#pragma mark - 调整输入框与关闭键盘
- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    if ([self.textView.text isEqualToString:@"我要评论"]) {
        self.textView.text=@"";
    }
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y-220, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (BOOL)textViewShouldEndEditing:(UITextView *)textView
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y+220, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if (range.location>=100)
    {
        return  NO;
    }
    else
    {
        return YES;
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.textView resignFirstResponder];
}

//按下Done按钮的调用方法，我们让键盘消失
-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [self.accountField resignFirstResponder];
    
    return YES;
}

//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.accountField resignFirstResponder];
}

- (void)clickXx:(id)sender
{
    [self.textView resignFirstResponder];
    [self.accountField resignFirstResponder];
    NSString * acc=self.accountField.text;
    if (![acc isEqualToString:@""]){
        if ([acc stringByTrimmingCharactersInSet: [NSCharacterSet decimalDigitCharacterSet]].length >0) {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入合法数字" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
            return;
        }
    }else{
        acc=@" ";
    }
    
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?rid=%@&kouwei=%f&huanjing=%f&fuwu=%f&xingjiabi=%f&content=%@&grade=%f&cost=%@",[Helper getIp],updateComment_url,rid,customNumberOfStars.rating,fwNumberOfStars.rating,hjNumberOfStars.rating,xjbNumberOfStars.rating,[self.textView.text stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding],ztNumberOfStars.rating,acc];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            if([response isEqualToString:@"success"]){
                //解析错误
                [Helper showHUD2:@"评价成功" andView:self.view andSize:100];
            }else{
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return NO;
}

#pragma mark -
#pragma mark Delegate implementation of NIB instatiated DLStarRatingControl

-(void)newRating:(RatingControl *)control :(float)rating {
    control.rating=rating;
    [self.textView resignFirstResponder];
    [self.accountField resignFirstResponder];
}
@end
