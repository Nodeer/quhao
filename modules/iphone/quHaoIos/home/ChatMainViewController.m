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

- (void)dealloc{
    self.messageToolView = nil;
    self.faceView = nil;
    
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardDidChangeFrameNotification object:nil];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _isFirst = YES;
    _iskeyUp = NO;
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    
    self.navigationItem.leftBarButtonItem = backButtonItem;
    [self initilzer];
    
    animationDuration = 0.25;
    [self setup];
    self.view.backgroundColor = [UIColor colorWithRed:248.0f/255 green:248.0f/255 blue:255.0f/255 alpha:1.0];
    _allMessagesFrame = [NSMutableArray array];
    
}

- (void)_reconnect;
{
    _webSocket.delegate = nil;
    [_webSocket close];
    if(self.image == nil || [self.image isEqualToString:@""]){
        self.image = @"user_chat";
    }
    NSString *str = [NSString stringWithFormat:@"ws://www.quhao.la:%@/websocket/room/socket?user=%@&uid=%@&image=%@&mid=%@",self.port,[self.user stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding],self.uid,self.image,self.mid];
    _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:str]]];
    _webSocket.delegate = self;
    
    self.title = [NSString stringWithFormat:@"%@-%@",self.mname,@"聊天室"];
    [_webSocket open];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self _reconnect];
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(keyboardWillShow:)
                                                name:UIKeyboardWillShowNotification
                                              object:nil];
    
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(keyboardWillHide:)
                                                name:UIKeyboardWillHideNotification
                                              object:nil];
    
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(keyboardChange:)
                                                name:UIKeyboardDidChangeFrameNotification
                                              object:nil];
}

//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *touch = [touches anyObject];
    if([touch.view isKindOfClass:[MessageTextView class]]||[touch.view isKindOfClass:[MessageInputView class]]||!_iskeyUp){
        return;
    }
    [self.messageToolView.messageInputTextView resignFirstResponder];
    [self messageViewAnimationWithMessageRect:CGRectZero
                     withMessageInputViewRect:self.messageToolView.frame
                                  andDuration:animationDuration
                                     andState:ZBMessageViewStateShowNone];
    _iskeyUp = NO;
}

#pragma mark -keyboard
- (void)keyboardWillHide:(NSNotification *)notification{
    
    keyboardRect = [[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    animationDuration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    _iskeyUp = NO;
}

- (void)keyboardWillShow:(NSNotification *)notification{
    keyboardRect = [[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    animationDuration= [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    _iskeyUp = YES;
}

- (void)keyboardChange:(NSNotification *)notification{
    if ([[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue].origin.y<CGRectGetHeight(self.view.frame)) {
        [self messageViewAnimationWithMessageRect:[[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue]
                         withMessageInputViewRect:self.messageToolView.frame
                                      andDuration:0.25
                                         andState:ZBMessageViewStateShowNone];
    }
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
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"房间人数已满,请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
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
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.removeFromSuperViewOnHide =YES;
    //hud.mode = MBProgressHUDModeText;
    hud.labelText = NSLocalizedString(@"链接超时,请重新进入", nil);
    hud.minSize = CGSizeMake(132.f, 108.0f);
    [hud hide:YES afterDelay:1.5];

    _webSocket = nil;
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
    [_allMessagesFrame removeAllObjects];
    _webSocket.delegate = nil;
    [_webSocket close];
    _webSocket = nil;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    long count = [_allMessagesFrame count]/2;
    [_allMessagesFrame removeObjectsInRange:NSMakeRange(0,count)];
    [self.tableView reloadData];
    //[_allMessagesFrame removeAllObjects];
}

- (void)sendMessage:(NSString *)inputText{
    _isFirst = NO;
    [_webSocket send:inputText];
    // 2、刷新表格
    [self.tableView reloadData];
    [self scrollTableView];
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
    static NSString *CellIdentifier = @"MessageCell";
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

- (void)setup
{
    self.tableView = [[UITableView alloc]initWithFrame:CGRectMake(0.0f,0.0f,kDeviceWidth, self.messageToolView.frame.origin.y-64) style:UITableViewStylePlain];
    [self.view addSubview:self.tableView];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.allowsSelection = NO;
    self.tableView.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"chat_bg_default.jpg"]];
    [self.view sendSubviewToBack:self.tableView];
//#if IOS7_SDK_AVAILABLE
//    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
//        [self.tableView setSeparatorInset:UIEdgeInsetsZero];
//    }
//    self.edgesForExtendedLayout = UIRectEdgeNone;
//    self.automaticallyAdjustsScrollViewInsets = NO;
//    self.navigationController.navigationBar.translucent = NO;
//    self.tabBarController.tabBar.translucent = NO;
//    self.extendedLayoutIncludesOpaqueBars = NO;
//#endif
    
}


- (void)messageViewAnimationWithMessageRect:(CGRect)rect  withMessageInputViewRect:(CGRect)inputViewRect andDuration:(double)duration andState:(ZBMessageViewState)state{
    
    [UIView animateWithDuration:duration animations:^{
        self.messageToolView.frame = CGRectMake(0.0f,CGRectGetHeight(self.view.frame)-CGRectGetHeight(rect)-CGRectGetHeight(inputViewRect),CGRectGetWidth(self.view.frame),CGRectGetHeight(inputViewRect));
        
        switch (state) {
            case ZBMessageViewStateShowFace:
            {
                self.faceView.frame = CGRectMake(0.0f,CGRectGetHeight(self.view.frame)-CGRectGetHeight(rect),CGRectGetWidth(self.view.frame),CGRectGetHeight(rect));
            }
                break;
            case ZBMessageViewStateShowNone:
            {
                self.faceView.frame = CGRectMake(0.0f,CGRectGetHeight(self.view.frame),CGRectGetWidth(self.view.frame),CGRectGetHeight(self.faceView.frame));
            }
                break;
                
            default:
                break;
        }
        _iskeyUp = YES;
        self.tableView.frame = CGRectMake(0.0f,0.0f,kDeviceWidth,self.messageToolView.frame.origin.y);
        [self scrollTableView];

    } completion:^(BOOL finished) {
        
    }];
}


-(void)scrollTableView
{
    if (_allMessagesFrame.count>0)
    {
        [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:_allMessagesFrame.count-1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
}

- (void)initilzer{
    
    CGFloat inputViewHeight;
    
    if ([[[UIDevice currentDevice]systemVersion]floatValue]>=7) {
        inputViewHeight = 45.0f;
    }
    else{
        inputViewHeight = 40.0f;
    }
    self.messageToolView = [[MessageInputView alloc]initWithFrame:CGRectMake(0.0f,
                                                                               self.view.frame.size.height - inputViewHeight,self.view.frame.size.width,inputViewHeight)];
    self.messageToolView.delegate = self;
    [self.view addSubview:self.messageToolView];
    
    [self shareFaceView];
    
}

- (void)shareFaceView{
    
    if (!self.faceView)
    {
        self.faceView = [[MessageManagerFaceView alloc]initWithFrame:CGRectMake(0.0f,
                                                                                  CGRectGetHeight(self.view.frame), CGRectGetWidth(self.view.frame), 196)];
        self.faceView.delegate = self;
        [self.view addSubview:self.faceView];
        
    }
}

- (void)didSendFaceAction:(BOOL)sendFace{
    if (sendFace) {
        [self messageViewAnimationWithMessageRect:self.faceView.frame
                         withMessageInputViewRect:self.messageToolView.frame
                                      andDuration:animationDuration
                                         andState:ZBMessageViewStateShowFace];
    }
    else{
        [self messageViewAnimationWithMessageRect:keyboardRect
                         withMessageInputViewRect:self.messageToolView.frame
                                      andDuration:animationDuration
                                         andState:ZBMessageViewStateShowNone];
    }
}

/*
 * 点击输入框代理方法
 */
- (void)inputTextViewWillBeginEditing:(MessageTextView *)messageInputTextView{
    
}

- (void)inputTextViewDidBeginEditing:(MessageTextView *)messageInputTextView
{
    [self messageViewAnimationWithMessageRect:keyboardRect
                     withMessageInputViewRect:self.messageToolView.frame
                                  andDuration:animationDuration
                                     andState:ZBMessageViewStateShowNone];
    
    if (!self.previousTextViewContentHeight)
    {
        self.previousTextViewContentHeight = messageInputTextView.contentSize.height;
    }
}
- (CGFloat)getTextViewContentH:(UITextView *)textView {
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0) {
        return ceilf([textView sizeThatFits:textView.frame.size].height);
    } else {
        return textView.contentSize.height;
    }
}

- (void)inputTextViewDidChange:(MessageTextView *)messageInputTextView
{
    if (!self.previousTextViewContentHeight)
    {
        self.previousTextViewContentHeight = messageInputTextView.contentSize.height;
    }
    CGFloat maxHeight = [MessageInputView maxHeight];
    CGFloat contentH = [self getTextViewContentH:messageInputTextView];
    
    BOOL isShrinking = contentH < self.previousTextViewContentHeight;
    CGFloat changeInHeight = contentH - _previousTextViewContentHeight;
    
    if (!isShrinking && (self.previousTextViewContentHeight == maxHeight || messageInputTextView.text.length == 0)) {
        changeInHeight = 0;
    }
    else {
        changeInHeight = MIN(changeInHeight, maxHeight - self.previousTextViewContentHeight);
    }
    
    if(changeInHeight != 0.0f) {
        
        [UIView animateWithDuration:0.01f
                         animations:^{
                             
                             if(isShrinking) {
                                 if ([[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
                                     self.previousTextViewContentHeight = MIN(contentH, maxHeight);
                                 }
                                 // if shrinking the view, animate text view frame BEFORE input view frame
                                 [self.messageToolView adjustTextViewHeightBy:changeInHeight];
                             }
                             
                             CGRect inputViewFrame = self.messageToolView.frame;
                             self.messageToolView.frame = CGRectMake(0.0f,
                                                                     inputViewFrame.origin.y - changeInHeight,
                                                                     inputViewFrame.size.width,
                                                                     inputViewFrame.size.height + changeInHeight);
                             
                             if(!isShrinking) {
                                 if ([[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
                                     self.previousTextViewContentHeight = MIN(contentH, maxHeight);
                                 }
                                 // growing the view, animate the text view frame AFTER input view frame
                                 [self.messageToolView adjustTextViewHeightBy:changeInHeight];
                             }
                             self.tableView.frame = CGRectMake(0.0f,0.0f,kDeviceWidth,self.messageToolView.frame.origin.y);
                             [self scrollTableView];
                         }
                         completion:^(BOOL finished) {
                             
                         }];
        
        self.previousTextViewContentHeight = MIN(contentH, maxHeight);
    }
    
    if (self.previousTextViewContentHeight == maxHeight) {
        double delayInSeconds = 0.01;
        dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(popTime,
                       dispatch_get_main_queue(),
                       ^(void) {
                           CGPoint bottomOffset = CGPointMake(0.0f, contentH - messageInputTextView.bounds.size.height);
                           [messageInputTextView setContentOffset:bottomOffset animated:YES];
                       });
    }
}
/*
 * 发送信息
 */
- (void)didSendTextAction:(MessageTextView *)messageInputTextView{
    NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
    NSTimeInterval now = [dat timeIntervalSince1970]*1;
    long currentDate = (long)now;
    if(currentDate - _lastDate<3){
        [Helper ToastNotification:@"亲,发送频率太高,请稍后再发" andView:self.view andLoading:NO andIsBottom:NO];
        return;
    }
    // 1、增加数据源
    if([Helper isConnectionAvailable]){
        [self sendMessage:messageInputTextView.text];
        
        [messageInputTextView setText:nil];
        [self inputTextViewDidChange:messageInputTextView];
        NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
        NSTimeInterval last = [dat timeIntervalSince1970]*1;
        _lastDate = (long)last;
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"当前网络不可用" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
}

#pragma end

#pragma mark - ZBMessageFaceViewDelegate
- (void)SendTheFaceStr:(NSString *)str
{
    if ([str isEqualToString:@"发送"]) {
        if (self.messageToolView.messageInputTextView.text.length>0) {
            NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
            NSTimeInterval now = [dat timeIntervalSince1970]*1;
            long currentDate = (long)now;
            if(currentDate - _lastDate<3){
                [Helper ToastNotification:@"亲,发送频率太高,请稍后再发" andView:self.view andLoading:NO andIsBottom:NO];
                return;
            }
            // 1、增加数据源
            if([Helper isConnectionAvailable]){
                [self sendMessage:self.messageToolView.messageInputTextView.text];
                [self.messageToolView.messageInputTextView setText:nil];
                [self inputTextViewDidChange:self.messageToolView.messageInputTextView];
                NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
                NSTimeInterval last = [dat timeIntervalSince1970]*1;
                _lastDate = (long)last;
            }else{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"当前网络不可用" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
            }
        }
    }else{
        self.messageToolView.messageInputTextView.text = [self.messageToolView.messageInputTextView.text stringByAppendingString:str];
        [self inputTextViewDidChange:self.messageToolView.messageInputTextView];
    }
}
@end