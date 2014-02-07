//
//  AppDelegate.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <UIKit/UIKit.h>

@interface LoadingCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel *lbl;
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *loading;

@end
