import java.util.Scanner;
import java.util.HashMap;

public class farm_game {
    public static void main(String[] args) {

        Storage storage = new Storage();
        Item wheat = new Item("wheat", 1);
        Entity.map_entity_product.put("cow", new Item("milk", 1));
        Entity.map_entity_food.put("cow", wheat);

        Field campo1 = new Field(storage);
        Enclosure recinto1 = new Enclosure("cow", storage);
        recinto1.add();
        storage.add(new Item("wheat", 9));
        System.out.println("Storage has " + new Item("milk", 1) + "? " + storage.storage.containsKey(wheat));
        System.out.println(wheat.equals(new Item("wheat", 1)));

        Scanner scan = new Scanner(System.in);
        welcome_msg();
        

        int input = -1;
        while(input != 9)
        {
            try
            {
                input = scan.nextInt();
            }
            catch(java.util.InputMismatchException t)
            {
                scan.next();
                input = -1;
            }

            switch (input)
            {
                case 0:
                welcome_msg();
                break;
                
                case (1): //vedi storage
                System.out.println(storage); 
                break;

                case (2): //raccogli il latte
                System.out.println("Raccogliendo il latte...");
                // System.out.println(recinto1.collect().toString());
                recinto1.collect();
                break;

                case (3): //nutri le mucche
                System.out.println("Nutrendo le mucche...");
                recinto1.feed();
                break;
               

                case (4): //aggiungi una mucca
                System.out.println("Comprando una mucca...");
                recinto1.add();
                break;

                case (5): 
                System.out.println("Seminando grano...");
                System.out.println(campo1.add(wheat));
                // System.out.println(storage.get(wheat));
                break;

                case 6:
                System.out.println("Raccogliendo grano...");
                campo1.collect();
                break;

                case (9): 
                System.out.println("Byee");
                break;

                default : System.out.println("Please insert a valid command");
            }
            System.out.println();
        }
        scan.close();
    }

    static void welcome_msg()
    {
        System.out.println("\nFarm game\nVersion 0.1 (almost workin)");
        System.out.println("\nPress: \t1 to check what is inside the storage,\n\t2 to collect the milk,");
        System.out.println("\t3 to feed the cow,");
        System.out.println("\t4 to add a new cow in the Enclosure,");
        System.out.println("\t5 to saw the field with wheat,");
        System.out.println("\t6 to collect the wheat from the field,");
        System.out.println("\t9 to quit, 0 for help.\n");
    }
}

class Field extends Place
{
    boolean is_sown = false; //è seminato?
    Item product = new Item("nothing", 0);
    Storage ref_storage;

    Field(Storage storage)
    {
        ref_storage = storage;
    }

    boolean add(Item item)
    {
        item.quantity = 1;
        if (this.is_sown == true)
            return false;
        if (ref_storage.get(item)) //se l'item è disponibile nello storage
        {
            is_sown = true;
            generate_product(item);
            return true;
        }
        return false;
    }

    private void generate_product(Item item)
    {
        this.product = item.copy();
        this.product.quantity = 2;
        is_sown = false;
        System.out.println("Il grano è cresciuto :)");
    }

    @Override
    void collect()
    {
        System.out.println("The field has " + this.product.toString());
        ref_storage.add(this.product);
    }
}



class Animal extends Entity
{
    boolean has_eaten = true;
    boolean is_alive;

    Animal(String tipo)
    {
        super(tipo); //inizializza l'entity
        is_alive = true;
        generate_product(); //sistema sta roba
    }

    Animal(String tipo, boolean bool)
    {
        super(tipo); //inizializza l'entity
        this.is_alive = bool;
        this.product.quantity = 0;
        if (bool == true)
            generate_product(); //sistema sta roba
    }

    void live()
    {
        this.is_alive = true;
        // System.out.println("Cow is alive!");
        generate_product();
    }

    @Override
    void generate_product()
    {
        has_eaten = false;
        // something
        this.product.quantity = 1;
        System.out.println("milk generated, now I have " + this.product.toString());
    }

    void eat()
    {
        has_eaten = true;
        System.out.println("Cow ate!");
        generate_product(); // anche sta cosa
    }

    @Override
    public String toString()
    {
        String to_return = "";
        if (this.is_alive)
            to_return += "I'm alive!\t" + "I got " + product.toString();
        // else 
        //     to_return += "I'm dead :(\t";
        // to_return = to_return + "I got " + product.toString(); 
        return to_return;
    }

    Animal copy()
    {
        Animal animal = new Animal(this.type, this.is_alive);
        return animal;
    }
}

class Enclosure extends Place
//Recinto dove si trovano gli animali
{
    Animal[] Livestock = new Animal[5]; //bestiame nel recinto
    Animal animal_type;
    Item product;

    Enclosure(String tipo, Storage stor)
    {
        this.storage = stor; //riferimento allo storage vero
        animal_type = new Animal(tipo, false);
        product = animal_type.product.copy();
        for (int i = 0; i < 5; i++) 
        {
            Livestock[i] = this.animal_type.copy();    
        }
    }

    boolean add()
    {
        for (Animal animal : Livestock)
        {
            if (animal.is_alive == false)
            {
                animal.live();
                return true;
            }           
        }
        return false;
    }

    
    boolean feed()
    {
        for (Animal animal : Livestock)
        {
            if (animal.has_eaten == false && animal.product.quantity == 0)
            {
                if (storage.get(animal_type.food))
                {
                    animal.eat();
                    return true;
                }
            }
        }
        return false;
    }

    void feed(int num)
    {
        for (int i = 0; i < num; i++)
            this.feed();
    }

    @Override
    void collect()
    {
        this.product.quantity = 0;
        for (Animal animal : Livestock)
        {
            if (animal.product.quantity > 0)
            {
                System.out.println("I collected " + animal.product.quantity + " milk");
                this.product.quantity += 1;
                animal.product.quantity = 0;
            }
        }
        System.out.println(this.product.quantity + " milk collected!");
        storage.add(this.product); 
    }

    @Override
    public String toString()
    {
        String to_return = "";
        for (Animal animal : Livestock)
        {
            if (animal.is_alive == true)
            {
                to_return = to_return + animal.toString() + "\n";
            }
        }
        return to_return;
    }

}



class Item
{
    String type;
    int quantity;

    // Item(String str)
    // {
    //     this.type = str;
    //     this.quantity = 0;
    // }

    Item(String str, int q)
    {
        this.type = str;
        this.quantity = q;
    }

    @Override
    public String toString()
    {
        return this.type + " (" + quantity + ")";
    }

    Item copy()
    {
        // Item item = new Item(this.type, this.quantity);
        // return item;
        return new Item(this.type, this.quantity);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        
        Item item = (Item) obj;
        return item.type == this.type;
    }

    @Override
    public int hashCode()
    {
        return (int) this.type.charAt(0) + (int) this.type.charAt(1);
    }
}


class Storage
{
    int MaxCapacity = 150;
    int number_of_items_stored = 0;
    HashMap<Item, Integer> storage = new HashMap<Item, Integer>();

    Storage()
    {
        
    }

    private int will_storage_be_full(int number_of_items_to_insert)
    {
        //ritorna -1 se c'è spazio, altrimenti
        //il numero di items che non entrano nello storage
        if (number_of_items_stored + number_of_items_to_insert < MaxCapacity)
            return -1;
        else
            return number_of_items_stored + number_of_items_to_insert - MaxCapacity;
    }

    private boolean are_there_enough(Item item)
    {
        if (storage.containsKey(item))
            return (storage.get(item) - item.quantity >= 0);
        else 
            return false;
    }
    

    void add(Item item)
    {
        System.out.println("Storage has " + item.toString() + "? " + storage.containsKey(item));
        
        int quantity = item.quantity;
        item.quantity = 1;

        if (will_storage_be_full(quantity) == -1) //se c'è spazio
        {
            number_of_items_stored += quantity;
            if (storage.containsKey(item))
                storage.put(item, storage.get(item) + quantity);
            else 
                storage.put(item, quantity);
            System.out.println("Every item has been added to the storage");
        }
        else //se non c'è spazio per tutto
        {
            number_of_items_stored = MaxCapacity;
            int items_remaining = will_storage_be_full(quantity); 
            // gli items rimasti fuori dallo storage
            if (storage.containsKey(item))
                storage.put(item, storage.get(item) + quantity - items_remaining);
            else 
                storage.put(item, quantity - items_remaining);

            System.out.println("Some items were thrown away :(");
            //che ci faccio con quelli che avanzano?
        }
    }

    boolean get(Item[] item_list)
    {
        //sottrae gli elementi richiesti da storage
        //se va a buon termine ritorna true, 
        //se non ci sono abbastanza items ritorna false
        for (Item item : item_list)
            if (!are_there_enough(item))
                return false;

        for (Item item : item_list)
        {
            storage.put(item, storage.get(item) - item.quantity);
            number_of_items_stored -= item.quantity;
        }
        
        return true;
    }

    boolean get(Item item)
    {
        //sottrae gli elementi richiesti da storage
        //se va a buon termine ritorna true, 
        //se non ci sono abbastanza items ritorna false
        System.out.println(are_there_enough(item));
        if (are_there_enough(item) == false)
            return false;

        storage.put(item, storage.get(item) - item.quantity);
        number_of_items_stored -= item.quantity;
                
        return true;
    }

    @Override
    public String toString()
    {
        String to_return = "This storage stores " + this.number_of_items_stored + " items\n";
        to_return += storage;
        return to_return;
    }

    
    // boolean containsKey(Item item)
    // {
    //     for(Item itom : storage.keySet())
    //     {
    //         if(itom.equals(item))
    //         {
    //             return true;
    //         }
    //     }
    //     return false;
    // }
}

abstract class Place 
{
    int[] dimensions = new int[2];
    Storage storage;
    abstract void collect();
}

abstract class Entity 
{
    static HashMap<String, Item> map_entity_product = new HashMap<>(); 
    static HashMap<String, Item> map_entity_food = new HashMap<>(); 
    //va riempita sta cosa
    String type;
    Item product;
    Item food;

    Entity(String tipo)
    {
        type = tipo;
        product = map_entity_product.get(tipo).copy();
        food = map_entity_food.get(tipo).copy();
    }

    abstract void generate_product();
}


/*
Storage ()
Item
Animali:
    Mucche
Recinto
Crops:
    Grano


Condizioni iniziali:


Comandi che posso dare:
 - Inserire nuovi
    - pascoli
    - orti
    - mucche
    - foraggio e grano
 - raccoliere
    - latte dalle mucche
    - 2 * grano dal grano seminato
 - vedere cosa ho nel magazzino





*/