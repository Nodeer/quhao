//
//  About.h
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <UIKit/UIKit.h>
#import "Helper.h"
@interface About : UIViewController

@property (strong, nonatomic) IBOutlet UILabel *lblVersion;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *lblOSC;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *lblCopyright;
@property (strong, nonatomic) IBOutlet UIImageView *img;

@end
