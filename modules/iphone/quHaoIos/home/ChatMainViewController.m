//
//  ChatMainViewController.m
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "ChatMainViewController.h"

@interface ChatMainViewController ()

@end

@implementation ChatMainViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _isFirst = YES;
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    
    self.navigationItem.leftBarButtonItem = backButtonItem;
    self.tableView=[[UITableView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight-108) style:UITableViewStylePlain];
    self.tableView.dataSource=self;
    self.tableView.delegate=self;
    //self.tableView.backgroundColor=[UIColor whiteColor];
    //self.tableView.indicatorStyle=UIScrollViewIndicatorStyleWhite;
    
#if IOS7_SDK_AVAILABLE
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
    }
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.navigationController.navigationBar.translucent = NO;
    self.tabBarController.tabBar.translucent = NO;
    self.extendedLayoutIncludesOpaqueBars = NO;
#endif
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.allowsSelection = NO;
    self.tableView.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"chat_bg_default.jpg"]];
    [self.view addSubview:self.tableView];
    self.view.backgroundColor = [UIColor whiteColor];
    //NSArray *array = [NSArray arrayWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"messages" ofType:@"plist"]];
    
    _allMessagesFrame = [NSMutableArray array];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    
    _footview = [[UIView alloc] init];
    _footview.frame = CGRectMake(0, kDeviceHeight-108, kDeviceWidth, 44);
    self.originalFrame = CGRectMake(0, kDeviceHeight-108, kDeviceWidth, 44);
    _originalFrame = _footview.frame;
    UIImageView * imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"toolbar_bottom_bar"]];
    imageView.frame = CGRectMake(0, 0, kDeviceWidth, 44);
    [_footview addSubview:imageView];
    
    UIButton * volumnBtn = [[UIButton alloc] initWithFrame:CGRectMake(14, 5, 34, 34)];
    [volumnBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_nor"] forState:UIControlStateNormal];
    [volumnBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_press"] forState:UIControlStateHighlighted];
    [volumnBtn addTarget:self action:@selector(voiceBtnClick:) forControlEvents:UIControlEventTouchDown];
    [_footview addSubview:volumnBtn];
    
    UIButton * smileBtn = [[UIButton alloc] initWithFrame:CGRectMake(222, 7, 34, 34)];
    [smileBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_smile_nor"] forState:UIControlStateNormal];
    //[smileBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_press"] forState:UIControlStateHighlighted];
    //[smileBtn addTarget:self action:@selector(emojeBtnClick:) forControlEvents:UIControlEventTouchDown];
    [_footview addSubview:smileBtn];
    
//    UIButton * addBtn = [[UIButton alloc] initWithFrame:CGRectMake(264, 7, 34, 34)];
//    [addBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_up_nor"] forState:UIControlStateNormal];
//    //[addBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_press"] forState:UIControlStateHighlighted];
//    [_footview addSubview:addBtn];
    
    _messageField = [[UITextField alloc] initWithFrame:CGRectMake(56, 7, 146, 30)];
    [_messageField setBackground:[UIImage imageNamed:@"chat_bottom_textfield"]];
    _messageField.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 10, 0)];
    _messageField.leftViewMode = UITextFieldViewModeAlways;
    _messageField.delegate = self;
    _messageField.placeholder = @""; //默认显示的字
    _messageField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _messageField.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    _messageField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    _messageField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    _messageField.returnKeyType = UIReturnKeySend;  //键盘返回类型
    _messageField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    _messageField.delegate = self;
    [_footview addSubview:_messageField];
    
    _speakBtn = [[UIButton alloc] initWithFrame:CGRectMake(56, 6, 146, 32)];
    [_speakBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_nor"] forState:UIControlStateNormal];
    //[syBtn setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_press"] forState:UIControlStateHighlighted];
    [_speakBtn setHidden:YES];
    [_footview addSubview:_speakBtn];
    [self.view addSubview:_footview];
}

- (void)_reconnect;
{
    _webSocket.delegate = nil;
    [_webSocket close];
    if(self.image == nil || [self.image isEqualToString:@""]){
        self.image = @"user_chat";
    }
    NSString *str = [NSString stringWithFormat:@"ws://www.quhao.la:9000/websocket/room/socket?user=%@&uid=%@&image=%@&mid=%@",self.user,self.uid,self.image,self.mid];
    _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:str]]];
    _webSocket.delegate = self;
    
    self.title = @"聊天室";
    [_webSocket open];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self _reconnect];
}

- (void)reconnect:(id)sender;
{
    [self _reconnect];
}

- (void)webSocketDidOpen:(SRWebSocket *)webSocket;
{
    //NSLog(@"Websocket Connected");
    //self.title = @"Connected!";
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error;
{
    //NSLog(@":( Websocket Failed With Error %@", error);
    //self.title = @"Connection Failed! (see logs)";
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"网络异常,请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
    [alert show];
    _webSocket = nil;
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message;
{
    //NSLog(@"Receivd \"%@\"", message);
    NSArray * result = [(NSString *)message componentsSeparatedByString:@":"];
    if([result[0] isEqualToString:@"message"]){
        NSString * temp = [NSString stringWithFormat:@"%@:%@:%@:%@:",result[0],result[1],result[2],result[3]];
        NSString * messageResult=[(NSString *)message stringByReplacingOccurrencesOfString:temp withString:@""];
        [self addMessageWithContent:messageResult img:result[3] withUid:result[2]];
        [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:[NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0]] withRowAnimation:UITableViewRowAnimationNone];
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
        if(_isFirst){
            [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:NO];
        }else{
            [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
        }
    }
    
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean;
{
    //NSLog(@"WebSocket closed");
    //self.title = @"Connection Closed! (see logs)";
    _webSocket = nil;
}


//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
    
    _webSocket.delegate = nil;
    [_webSocket close];
    _webSocket = nil;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - 键盘处理
#pragma mark 键盘即将显示
//- (void)keyBoardWillShow:(NSNotification *)note{
//    
//    CGRect rect = [note.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
//    CGFloat ty = - rect.size.height;
//    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue] animations:^{
//        self.view.transform = CGAffineTransformMakeTranslation(0, ty);
//    }];
//    
//}
//#pragma mark 键盘即将退出
//- (void)keyBoardWillHide:(NSNotification *)note{
//    
//    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue] animations:^{
//        self.view.transform = CGAffineTransformIdentity;
//    }];
//}

-(void)setOriginalFrame:(CGRect)originalFrame
{
    _footview.frame = CGRectMake(0, CGRectGetMinY(originalFrame), 320, CGRectGetHeight(originalFrame));
}

- (void)keyBoardWillShow:(NSNotification*)notification{
    CGRect _keyboardRect = [[[notification userInfo] objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    //NSLog(@"%f-%f-%f-%f",_keyboardRect.origin.y,_keyboardRect.size.height,[self getHeighOfWindow]-CGRectGetMaxY(self.frame),CGRectGetMinY(self.frame));
    
    //如果self在键盘之下 才做偏移
    if ([self convertYToWindow:CGRectGetMaxY(self.originalFrame)]>=_keyboardRect.origin.y)
    {
        //没有偏移 就说明键盘没出来，使用动画
        if (_footview.frame.origin.y== self.originalFrame.origin.y) {
            [UIView animateWithDuration:0.3
                                  delay:0
                                options:UIViewAnimationOptionCurveEaseInOut
                             animations:^{
                                 self.tableView.transform = CGAffineTransformMakeTranslation(0, -_keyboardRect.size.height);
                             } completion:nil];
            
            [UIView animateWithDuration:0.3
                                  delay:0
                                options:UIViewAnimationOptionCurveEaseInOut
                             animations:^{
                                 _footview.transform = CGAffineTransformMakeTranslation(0, -_keyboardRect.size.height);
                             } completion:nil];
        }
        else
        {
            _footview.transform = CGAffineTransformMakeTranslation(0, -_keyboardRect.size.height);
            self.tableView.transform = CGAffineTransformMakeTranslation(0, -_keyboardRect.size.height);
        }
    }
    else
    {
        
    }
    
}

- (void)keyBoardWillHide:(NSNotification*)notification{
    
    
    [UIView animateWithDuration:0.3
                          delay:0
                        options:UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         _footview.transform = CGAffineTransformMakeTranslation(0, 0);
                     } completion:nil];
    
    [UIView animateWithDuration:0.3
                          delay:0
                        options:UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         self.tableView.transform = CGAffineTransformMakeTranslation(0, 0);
                     } completion:nil];
}

-(float)getHeighOfWindow
{
    return kDeviceHeight;
}

#pragma  mark ConvertPoint
//将坐标点y 在window和superview转化  方便和键盘的坐标比对
-(float)convertYFromWindow:(float)Y
{
    CGPoint o = [self.view.superview.window convertPoint:CGPointMake(0, Y) toView:_footview.superview];
    return o.y;
    
}

-(float)convertYToWindow:(float)Y
{
    CGPoint o = [_footview.superview convertPoint:CGPointMake(0, Y) toView:self.view.superview.window];
    return o.y;
    
}

#pragma mark - 文本框代理方法
#pragma mark 点击textField键盘的回车按钮
- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
    NSTimeInterval now = [dat timeIntervalSince1970]*1;
    long currentDate = (long)now;
    if(currentDate - _lastDate<3){
        [Helper ToastNotification:@"亲,发送频率太高,请稍后再发" andView:self.view andLoading:NO andIsBottom:NO];

        return NO;
    }
    _isFirst = NO;
    // 1、增加数据源
    if([Helper isConnectionAvailable]){
        NSString *content = textField.text;
        [_webSocket send:content];
        // 2、刷新表格
        [self.tableView reloadData];
        // 3、滚动至当前行
        if(_allMessagesFrame.count>0){
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:_allMessagesFrame.count - 1 inSection:0];
            [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
        }
        // 4、清空文本框内容
        _messageField.text = nil;
        NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
        NSTimeInterval last = [dat timeIntervalSince1970]*1;
        _lastDate = (long)last;
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"当前网络不可用" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
    return YES;
}

- (void)addMessageWithContent:(NSString *)content img:(NSString *)image withUid:(NSString *)uidFormSever{
    
    MessageFrame *mf = [[MessageFrame alloc] init];
    Message *msg = [[Message alloc] init];
    msg.content = content;
    msg.time = @"";
    if([self.uid isEqualToString:uidFormSever]){
        msg.type = MessageTypeMe;
        if([image isEqualToString:@"user_chat"]){
            msg.icon = @"icon02.jpg";
        }else{
            msg.icon = image;
        }
    }else{
        msg.type = MessageTypeOther;
        if([image isEqualToString:@"user_chat"]){
            msg.icon = @"icon02.jpg";
        }else{
            msg.icon = image;
        }
    }
    mf.message = msg;
    
    [_allMessagesFrame addObject:mf];
}

#pragma mark - tableView数据源方法

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _allMessagesFrame.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    MessageCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil) {
        cell = [[MessageCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    // 设置数据
    cell.messageFrame = _allMessagesFrame[indexPath.row];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return [_allMessagesFrame[indexPath.row] cellHeight];
}

#pragma mark - 代理方法

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self.view endEditing:YES];
}

//-(void)emojeBtnClick:(UIButton *)sender {
//    if (_messageField.hidden) { //输入框隐藏，按住说话按钮显示
//        _messageField.hidden = NO;
//        _speakBtn.hidden = YES;
//        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_smile_nor"] forState:UIControlStateNormal];
//        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_press.png"] forState:UIControlStateHighlighted];
//        [_messageField becomeFirstResponder];
//    }else{ //输入框处于显示状态，按住说话按钮处于隐藏状态
//        _messageField.hidden = YES;
//        _speakBtn.hidden = NO;
//        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_smile_nor"] forState:UIControlStateNormal];
//        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_keyboard_press.png"] forState:UIControlStateHighlighted];
//        [_messageField resignFirstResponder];
//    }
//}

#pragma mark - 语音按钮点击
- (void)voiceBtnClick:(UIButton *)sender {
    if (_messageField.hidden) { //输入框隐藏，按住说话按钮显示
        _messageField.hidden = NO;
        _speakBtn.hidden = YES;
        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_nor.png"] forState:UIControlStateNormal];
        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_voice_press.png"] forState:UIControlStateHighlighted];
        [_messageField becomeFirstResponder];
    }else{ //输入框处于显示状态，按住说话按钮处于隐藏状态
        _messageField.hidden = YES;
        _speakBtn.hidden = NO;
        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_keyboard_nor.png"] forState:UIControlStateNormal];
        [sender setBackgroundImage:[UIImage imageNamed:@"chat_bottom_keyboard_press.png"] forState:UIControlStateHighlighted];
        [_messageField resignFirstResponder];
    }
}
@end