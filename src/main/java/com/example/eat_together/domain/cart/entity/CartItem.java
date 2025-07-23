@Entity
@Getter
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private int quantity;

    public static CartItem of(Menu menu, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.menu = menu;
        cartItem.quantity = quantity;
        return cartItem;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return (int)(menu.getPrice() * quantity);
    }
}
