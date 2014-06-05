//
//  FacialView.h
//  quHaoIos
//
//  Created by sam on 14-5-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//
#import <UIKit/UIKit.h>
#import "EmojiEmoticons.h"
#import "Emoji.h"
#import "EmojiMapSymbols.h"
#import "EmojiPictographs.h"
#import "EmojiTransport.h"
@protocol facialViewDelegate

-(void)selectedFacialView:(NSString*)str;

@end


@interface FacialView : UIView {
    NSArray *faces;
}
@property(nonatomic,assign)id<facialViewDelegate>delegate;

-(void)loadFacialView:(int)page size:(CGSize)size;

@end
