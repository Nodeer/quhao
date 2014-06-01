//
//  ChatMainViewController.h
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MessageFrame.h"
#import "Message.h"
#import "MessageCell.h"
#import "SRWebSocket.h"
@interface ChatMainViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate, SRWebSocketDelegate>
{
    NSMutableArray  *_allMessagesFrame;
    SRWebSocket *_webSocket;
    BOOL _isFirst;
    UIView * _footview;
    long _lastDate;
}
@property (strong, nonatomic)  UITableView *tableView;
@property (strong, nonatomic)  UITextField *messageField;
@property (strong, nonatomic)  UIButton *speakBtn;
@property (strong, nonatomic)  NSString *uid;
@property (strong, nonatomic)  NSString *user;
@property (strong, nonatomic)  NSString *image;
@property (strong, nonatomic)  NSString *mid;
@property(assign,nonatomic)CGRect originalFrame;

- (void)voiceBtnClick:(UIButton *)sender;

@end
