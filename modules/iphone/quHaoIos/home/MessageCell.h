//
//  MessageCell.h
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EGOImageView.h"
#import "Helper.h"
@class MessageFrame;

@interface MessageCell : UITableViewCell

@property (nonatomic, strong) MessageFrame *messageFrame;

@end
