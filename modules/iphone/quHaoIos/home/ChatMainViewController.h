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
#import "MessageInputView.h"
#import "MessageManagerFaceView.h"
#import "Helper.h"
typedef NS_ENUM(NSInteger,ZBMessageViewState) {
    ZBMessageViewStateShowFace,
    ZBMessageViewStateShowShare,
    ZBMessageViewStateShowNone,
};

@interface ChatMainViewController : UIViewController<MessageInputViewDelegate,MessageManagerFaceViewDelegate,UITableViewDataSource, UITableViewDelegate, SRWebSocketDelegate>
{
    NSMutableArray  *_allMessagesFrame;
    SRWebSocket *_webSocket;
    BOOL _isFirst;
    long _lastDate;
    double animationDuration;
    CGRect keyboardRect;
}

@property (strong, nonatomic)  UITableView *tableView;
@property (strong, nonatomic)  NSString *uid;
@property (strong, nonatomic)  NSString *user;
@property (strong, nonatomic)  NSString *image;
@property (strong, nonatomic)  NSString *mid;
@property (strong, nonatomic)  NSString *mname;
@property (nonatomic,strong) MessageInputView *messageToolView;

@property (nonatomic,strong) MessageManagerFaceView *faceView;


@property (nonatomic,assign) CGFloat previousTextViewContentHeight;

- (void)sendMessage:(NSString *)message;

- (void)messageViewAnimationWithMessageRect:(CGRect)rect  withMessageInputViewRect:(CGRect)inputViewRect andDuration:(double)duration andState:(ZBMessageViewState)state;

@end
